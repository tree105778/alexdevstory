package com.alexdevstory.backend.repository;

import com.alexdevstory.backend.entity.BlogPost;
import com.alexdevstory.backend.entity.BlogTag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BlogPostRepositoryTest {
    @PersistenceContext
    EntityManager em;

    @Autowired
    BlogPostRepository blogPostRepository;

    @Autowired
    BlogTagRepository blogTagRepository;

    @Autowired
    BlogImageRepository blogImageRepository;

    @Test
    void blogPostRepositoryTimerTest() throws InterruptedException {
        BlogPost post = BlogPost.builder()
                .title("test title")
                .content("test content")
                .build();

        BlogPost savedPost = blogPostRepository.save(post);
        assertThat(savedPost.getCreatedDate()).isNotNull();
        assertThat(savedPost.getLastModifiedDate()).isNotNull();
        assertThat(savedPost.getCreatedDate()).isEqualTo(savedPost.getLastModifiedDate());

        System.out.println("savedPost.getId() = " + savedPost.getId());
        System.out.println("savedPost.getTitle() = " + savedPost.getTitle());
        System.out.println("savedPost.getContent() = " + savedPost.getContent());
        System.out.println("savedPost.getCreatedDate() = " + savedPost.getCreatedDate());
        System.out.println("savedPost.getLastModifiedDate() = " + savedPost.getLastModifiedDate());

        //업데이트 시간이 정확한지 확인하기
        Thread.sleep(3000);
        BlogPost modifiedPost = savedPost.editBlogPost("updated title", "updated content");
        em.flush();
//        BlogPost modifiedPost = blogPostRepository.save(savedPost);
        assertThat(modifiedPost.getLastModifiedDate()).isNotNull();
        assertThat(modifiedPost.getLastModifiedDate()).isAfter(modifiedPost.getCreatedDate());

        System.out.println("modifiedPost.getId() = " + modifiedPost.getId());
        System.out.println("modifiedPost.getTitle() = " + modifiedPost.getTitle());
        System.out.println("modifiedPost.getContent() = " + modifiedPost.getContent());
        System.out.println("modifiedPost.getCreatedDate() = " + modifiedPost.getCreatedDate());
        System.out.println("modifiedPost.getLastModifiedDate() = " + modifiedPost.getLastModifiedDate());
    }

    @Test
    void blogPostRepositoryTest() {
        BlogTag firstTag = new BlogTag("first tag");
        BlogTag secondTag = new BlogTag("second tag");

        blogTagRepository.save(firstTag);
        blogTagRepository.save(secondTag);

        BlogPost blogPost = BlogPost.builder()
                .title("abc")
                .content("hello! world")
                .build();

        blogPost.addTag(firstTag);
        blogPost.addTag(secondTag);

        BlogPost savedPost = blogPostRepository.save(blogPost);

        //블로그 포스트 리포지토리에서 가져온 BlogPost가 원본과 동일한 지 검증
        Optional<BlogPost> post = blogPostRepository.findById(blogPost.getId());
        assertThat(post).isPresent();
        assertThat(post.get().getTitle()).isEqualTo(savedPost.getTitle());
        assertThat(post.get().getContent()).isEqualTo(savedPost.getContent());
        assertThat(post.get().getTags().size()).isEqualTo(savedPost.getTags().size());

        //BlogPostRepository findBlogPostsByTagName 메소드 검증
        List<BlogPost> findBlogPost = blogPostRepository.findBlogPostsByTagName(firstTag.getTag());
        assertThat(findBlogPost.size()).isEqualTo(1);
    }

    @Test
    void findBlogPostsByTagName() {
        BlogTag firstTag = blogTagRepository.save(new BlogTag("first tag"));
        BlogTag secondTag = blogTagRepository.save(new BlogTag("second tag"));

        BlogPost firstPost = BlogPost.builder()
                .title("first post")
                .content("content for first post")
                .build();

        BlogPost secondPost = BlogPost.builder()
                .title("second post")
                .content("content for second post")
                .build();
        firstPost.addTag(firstTag);
        firstPost.addTag(secondTag);
        secondPost.addTag(firstTag);

        BlogPost savedFirstPost = blogPostRepository.save(firstPost);
        BlogPost savedSecondPost = blogPostRepository.save(secondPost);

        List<BlogPost> postsWithFirstTag = blogPostRepository.findBlogPostsByTagName(firstTag.getTag());
        List<BlogPost> postsWithSecondTag = blogPostRepository.findBlogPostsByTagName(secondTag.getTag());

        //검증
        assertThat(postsWithFirstTag).hasSize(2);
        assertThat(postsWithFirstTag.get(0).getTitle()).isIn(firstPost.getTitle(), secondPost.getTitle());
        assertThat(postsWithFirstTag.get(1).getTitle()).isIn(firstPost.getTitle(), secondPost.getTitle());
        assertThat(postsWithFirstTag.get(0).getContent()).isIn(firstPost.getContent(), secondPost.getContent());
        assertThat(postsWithFirstTag.get(1).getContent()).isIn(firstPost.getContent(), secondPost.getContent());

        assertThat(postsWithSecondTag).hasSize(1);
        assertThat(postsWithSecondTag.get(0).getTitle()).isEqualTo(firstPost.getTitle());
        assertThat(postsWithSecondTag.get(0).getContent()).isEqualTo(firstPost.getContent());
    }

    @Test
    void findAllByPageable() {
        List<BlogPost> blogPosts = new ArrayList<>(40);
        BlogTag tag1 = blogTagRepository.save(new BlogTag("tag1"));
        BlogTag tag2 = blogTagRepository.save(new BlogTag("tag2"));
        for (int i = 1; i <= 40; i++) {
            BlogPost blogPost = BlogPost.builder()
                    .title("test title" + i)
                    .content("test content" + i)
                    .build();
            blogPost.addTag(tag1);
            if (i % 2 == 0) {
                blogPost.addTag(tag2);
            }
            blogPosts.add(blogPostRepository.save(blogPost));
        }

        Page<BlogPost> result = blogPostRepository.findAll(PageRequest.of(0, 8));

        assertThat(result).hasSize(8);
        assertThat(result.getContent()).containsExactlyElementsOf(blogPosts.subList(0, 8));
    }
}