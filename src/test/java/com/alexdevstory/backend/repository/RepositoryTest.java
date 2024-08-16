package com.alexdevstory.backend.repository;

import com.alexdevstory.backend.entity.BlogImage;
import com.alexdevstory.backend.entity.BlogPost;
import com.alexdevstory.backend.entity.BlogTag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
    void blogTagRepositoryTest() {
        BlogTag firstTag = blogTagRepository.save(new BlogTag("first tag"));
        BlogTag secondTag = blogTagRepository.save(new BlogTag("second tag"));

        //블로그 테그 리포지토리에서 가져온 BlogTag가 저장한 원본과 동일한 지 검증
        Optional<BlogTag> findFirstTag = blogTagRepository.findByTag(firstTag.getTag());
        assertThat(findFirstTag.get().getTag()).isEqualTo(firstTag.getTag());

        Optional<BlogTag> findSecondTag = blogTagRepository.findByTag(secondTag.getTag());
        assertThat(findSecondTag.get().getTag()).isEqualTo(secondTag.getTag());
    }

    @Test
    void blogImageRepositoryTest() {
        BlogPost savedPost = BlogPost.builder()
                .title("Test Post")
                .content("This is a test post")
                .build();

        BlogImage blogImage1 = BlogImage.builder()
                .imageData("image1".getBytes())
                .fileName("image1/jpg")
                .contentType("image/jpeg")
                .blogPost(savedPost)
                .build();

        BlogImage blogImage2 = BlogImage.builder()
                .imageData("image2".getBytes())
                .fileName("image2/jpg")
                .contentType("image/jpeg")
                .blogPost(savedPost)
                .build();

        blogImageRepository.save(blogImage1);
        blogImageRepository.save(blogImage2);

        blogPostRepository.save(savedPost);

        //findBlogImagesByPostTitle 기능 검증하기
        List<BlogImage> findImages = blogImageRepository.findBlogImagesByPostTitle(savedPost.getTitle());

        assertThat(findImages).isNotNull();
        assertThat(findImages).hasSize(2);
        assertThat(findImages.get(0).getBlogPost().getTitle()).isEqualTo(savedPost.getTitle());
        assertThat(findImages.get(1).getBlogPost().getTitle()).isEqualTo(savedPost.getTitle());
        assertThat(findImages.get(0).getImageData()).isIn(blogImage1.getImageData(), blogImage2.getImageData());
        assertThat(findImages.get(1).getImageData()).isIn(blogImage1.getImageData(), blogImage2.getImageData());
        assertThat(findImages.get(0).getFileName()).isIn(blogImage1.getFileName(), blogImage2.getFileName());
        assertThat(findImages.get(1).getFileName()).isIn(blogImage1.getFileName(), blogImage2.getFileName());
    }
}