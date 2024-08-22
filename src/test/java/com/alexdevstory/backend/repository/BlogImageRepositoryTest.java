package com.alexdevstory.backend.repository;

import com.alexdevstory.backend.entity.BlogImage;
import com.alexdevstory.backend.entity.BlogPost;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BlogImageRepositoryTest {
    @Autowired
    BlogImageRepository blogImageRepository;
    @Autowired
    BlogPostRepository blogPostRepository;

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