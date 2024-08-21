package com.alexdevstory.backend.service;

import com.alexdevstory.backend.dto.BlogImageDTO;
import com.alexdevstory.backend.dto.BlogPostDTO;
import com.alexdevstory.backend.dto.BlogPostSummaryAndPageDTO;
import com.alexdevstory.backend.dto.BlogPostSummaryDTO;
import com.alexdevstory.backend.entity.BlogPost;
import com.alexdevstory.backend.entity.BlogTag;
import com.alexdevstory.backend.repository.BlogPostRepository;
import com.alexdevstory.backend.repository.BlogTagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BlogPostServiceTest {
    @Mock
    BlogPostRepository blogPostRepository;

    @Mock
    BlogTagRepository blogTagRepository;

    @InjectMocks
    BlogPostService blogPostService;

    private BlogPost blogPost;
    private BlogTag tag1;
    private BlogTag tag2;
    private Pageable pageable = PageRequest.of(0, 8);

    @BeforeEach
    void setUp() {
        tag1 = new BlogTag("tag1");
        tag2 = new BlogTag("tag2");

        blogPost = BlogPost.builder()
                .title("test title")
                .content("test content")
                .build();

        blogPost.addTag(tag1);
        blogPost.addTag(tag2);
    }

    @Test
    void saveBlogPost() {
        BlogPostDTO testPostDTO = BlogPostDTO.builder()
                .title("test title")
                .content("test content")
                .tags(List.of("tag1", "tag2"))
                .build();

        when(blogTagRepository.findByTag("tag1")).thenReturn(Optional.of(new BlogTag("tag1")));
        when(blogTagRepository.findByTag("tag2")).thenReturn(Optional.of(new BlogTag("tag2")));

        blogPostService.saveBlogPost(testPostDTO);

        verify(blogPostRepository, times(1)).save(any(BlogPost.class));
        verify(blogTagRepository, times(2)).findByTag(anyString());
        verify(blogTagRepository, never()).save(any(BlogTag.class));

    }

    @Test
    void editBlogPost() {
        BlogPost existingPost = new BlogPost("Original Title", "Original Content");
        BlogPostDTO editDTO = BlogPostDTO.builder()
                .title("Updated title")
                .content("Updated content")
                .tags(List.of("newTag"))
                .images(
                        List.of(BlogImageDTO.builder()
                                .imageData(new byte[]{4, 5, 6})
                                .fileName("newImage.jpg")
                                .contentType("image/jpeg")
                                .build())
                ).build();

        when(blogPostRepository.findByTitle(existingPost.getTitle())).thenReturn(Optional.of(existingPost));
        when(blogTagRepository.findByTag("newTag")).thenReturn(Optional.of(new BlogTag("newTag")));

        BlogPostDTO result = blogPostService.editBlogPost(existingPost.getTitle(), editDTO);

        assertThat(result.getTitle()).isEqualTo(editDTO.getTitle());
        assertThat(result.getContent()).isEqualTo(editDTO.getContent());
        assertThat(result.getTags()).hasSize(1);
        assertThat("newTag").isIn(result.getTags());
    }

    @Test
    void getBlogPostsBySearchCond() {
        when(blogPostRepository.findAll(any(Example.class), any(Pageable.class)))
                .thenReturn(new PageImpl(List.of(blogPost)));

        List<BlogPostSummaryDTO> results = blogPostService.getBlogPostsBySearchCond("test", pageable).getContent();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo(blogPost.getTitle());
        assertThat(results.get(0).getSummary()).isEqualTo(blogPost.getContent());

        verify(blogPostRepository, times(1)).findAll(any(Example.class), any(Pageable.class));
    }

    @Test
    void getBlogPostsByTags() {
        BlogPost blogPost2 = BlogPost.builder()
                .title("test title2")
                .content("test content2")
                .build();
        blogPost2.addTag(tag1);
        when(blogPostRepository.findBlogPostsByTags(anyList(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(blogPost, blogPost2)));
        BlogPostSummaryAndPageDTO results =
                blogPostService.getBlogPostsByTags(List.of(tag1.getTag()), pageable);

        assertThat(results.getSummaries()).hasSize(2);
        assertThat(results.getSummaries()).containsExactlyInAnyOrder(
                new BlogPostSummaryDTO(blogPost.getTitle(), blogPost.getContent()),
                new BlogPostSummaryDTO(blogPost2.getTitle(), blogPost2.getContent()));

        verify(blogPostRepository, times(1))
                .findBlogPostsByTags(anyList(), any(Pageable.class));
    }

    @Test
    void getAllBlogPostSummary() {
        when(blogPostRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(blogPost)));
        BlogPostSummaryAndPageDTO results = blogPostService.getAllBlogPostSummary(pageable);

        assertThat(results.getSummaries()).hasSize(1);
        assertThat(results.getSummaries().get(0).getTitle()).isEqualTo(blogPost.getTitle());
        assertThat(results.getSummaries().get(0).getSummary()).isEqualTo(blogPost.getContent());

        verify(blogPostRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getBlogPost() {
        when(blogPostRepository.findByTitle(anyString())).thenReturn(Optional.of(blogPost));

        BlogPostDTO result = blogPostService.getBlogPost(blogPost.getTitle());

        assertThat(result.getTitle()).isEqualTo(blogPost.getTitle());
        assertThat(result.getContent()).isEqualTo(blogPost.getContent());

        verify(blogPostRepository, times(1)).findByTitle(anyString());
    }

    @Test
    void deleteBlogPost() {
        when(blogPostRepository.findByTitle(anyString())).thenReturn(Optional.of(blogPost));

        boolean isDeleted = blogPostService.deleteBlogPost(blogPost.getTitle());

        assertThat(isDeleted).isTrue();
        verify(blogPostRepository, times(1)).delete(any(BlogPost.class));
    }
}