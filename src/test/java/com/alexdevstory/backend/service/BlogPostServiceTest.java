package com.alexdevstory.backend.service;

import com.alexdevstory.backend.dto.BlogImageDTO;
import com.alexdevstory.backend.dto.BlogPostDTO;
import com.alexdevstory.backend.entity.BlogPost;
import com.alexdevstory.backend.entity.BlogTag;
import com.alexdevstory.backend.repository.BlogPostRepository;
import com.alexdevstory.backend.repository.BlogTagRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BlogPostServiceTest {
    @Mock
    BlogPostRepository blogPostRepository;

    @Mock
    BlogTagRepository blogTagRepository;

    @InjectMocks
    BlogPostService blogPostService;

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
        assertThat(result.getTags().get(0)).isIn(editDTO.getTags());
    }

    @Test
    void getAllBlogPostSummary() {
    }

    @Test
    void getBlogPost() {
    }

    @Test
    void deleteBlogPost() {
    }
}