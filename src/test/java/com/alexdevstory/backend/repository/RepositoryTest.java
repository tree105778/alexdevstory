package com.alexdevstory.backend.repository;

import com.alexdevstory.backend.entity.BlogPost;
import com.alexdevstory.backend.entity.BlogTag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RepositoryTest {
    @Autowired
    BlogPostRepository blogPostRepository;

    @Autowired
    BlogTagRepository blogTagRepository;

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
    void blogTagRepositoryTest() {
        BlogTag firstTag = blogTagRepository.save(new BlogTag("first tag"));
        BlogTag secondTag = blogTagRepository.save(new BlogTag("second tag"));

        //블로그 테그 리포지토리에서 가져온 BlogTag가 저장한 원본과 동일한 지 검증
        Optional<BlogTag> findFirstTag = blogTagRepository.findByTag(firstTag.getTag());
        assertThat(findFirstTag.get().getTag()).isEqualTo(firstTag.getTag());

        Optional<BlogTag> findSecondTag = blogTagRepository.findByTag(secondTag.getTag());
        assertThat(findSecondTag.get().getTag()).isEqualTo(secondTag.getTag());
    }

}