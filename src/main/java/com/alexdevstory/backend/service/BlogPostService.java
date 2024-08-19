package com.alexdevstory.backend.service;

import com.alexdevstory.backend.dto.BlogImageDTO;
import com.alexdevstory.backend.dto.BlogPostDTO;
import com.alexdevstory.backend.dto.BlogPostSummaryDTO;
import com.alexdevstory.backend.entity.BlogImage;
import com.alexdevstory.backend.entity.BlogPost;
import com.alexdevstory.backend.entity.BlogTag;
import com.alexdevstory.backend.exception.BlogPostNotFoundException;
import com.alexdevstory.backend.repository.BlogPostRepository;
import com.alexdevstory.backend.repository.BlogTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogPostService {
    private final BlogPostRepository blogPostRepository;
    private final BlogTagRepository blogTagRepository;

    public void saveBlogPost(BlogPostDTO blogPostDTO) {
        BlogPost post = BlogPost.builder()
                .title(blogPostDTO.getTitle())
                .content(blogPostDTO.getContent())
                .build();

        blogPostDTO.getTags().forEach(tag -> {
            BlogTag blogTag = blogTagRepository.findByTag(tag)
                    .orElseGet(() -> blogTagRepository.save(new BlogTag(tag)));
            post.addTag(blogTag);
        });
        blogPostDTO.getImages().forEach(image -> {
            BlogImage blogImage = BlogImage.builder()
                    .imageData(image.getImageData())
                    .fileName(image.getFileName())
                    .contentType(image.getContentType())
                    .blogPost(post)
                    .build();
            post.addImage(blogImage);
        });
        blogPostRepository.save(post);
    }

    @Transactional
    public BlogPostDTO editBlogPost(String title, BlogPostDTO editBlogPostDTO) {
        BlogPost blogPost = blogPostRepository.findByTitle(title).orElseThrow(
                () -> new BlogPostNotFoundException("No Blog Post title:" + title));

        blogPost.editBlogPost(editBlogPostDTO.getTitle(), editBlogPostDTO.getContent());

        optimizeTagUpdate(editBlogPostDTO, blogPost);
        imageUpdate(editBlogPostDTO, blogPost);

        blogPostRepository.save(blogPost);

        return convertToDTO(blogPost);
    }


    public List<BlogPostSummaryDTO> getAllBlogPostSummary(Pageable pageable) {
        return blogPostRepository.findAll(pageable).stream()
                .map(blogPost -> new BlogPostSummaryDTO(blogPost.getTitle(), blogPost.getContent()))
                .collect(Collectors.toList());
    }

    public BlogPostDTO getBlogPost(String title) {
        BlogPost blogPost = blogPostRepository.findByTitle(title).orElseThrow(
                () -> new BlogPostNotFoundException("No Blog Post title:" + title));

        return convertToDTO(blogPost);
    }

    private BlogPostDTO convertToDTO(BlogPost blogPost) {
        List<String> tagList = blogPost.getTags().stream()
                .sorted(Comparator.comparing(postTag -> postTag.getTag().getTag()))
                .map(postTag -> postTag.getTag().getTag())
                .collect(Collectors.toList());

        List<BlogImageDTO> blogImageDTOList = blogPost.getImages().stream()
                .map(blogImage -> BlogImageDTO.builder()
                        .imageData(blogImage.getImageData())
                        .fileName(blogImage.getFileName())
                        .contentType(blogImage.getContentType())
                        .build()
                ).collect(Collectors.toList());

        return BlogPostDTO.builder()
                .title(blogPost.getTitle())
                .content(blogPost.getContent())
                .tags(tagList)
                .images(blogImageDTOList)
                .build();
    }

    @Transactional
    public boolean deleteBlogPost(String title) {
        BlogPost findPost = blogPostRepository.findByTitle(title).orElseThrow(
                () -> new BlogPostNotFoundException("No Blog Post title:" + title));
        blogPostRepository.delete(findPost);

        return true;
    }

    private void imageUpdate(BlogPostDTO editBlogPostDTO, BlogPost blogPost) {
        blogPost.getImages().clear();
        editBlogPostDTO.getImages().forEach(blogImageDTO -> {
            BlogImage blogImage = BlogImage.builder()
                    .imageData(blogImageDTO.getImageData())
                    .fileName(blogImageDTO.getFileName())
                    .contentType(blogImageDTO.getContentType())
                    .blogPost(blogPost)
                    .build();
            blogPost.addImage(blogImage);
        });
    }

    private void optimizeTagUpdate(BlogPostDTO editBlogPostDTO, BlogPost blogPost) {
        //태그 업데이트 최적화
        Set<String> existingTags = blogPost.getTags().stream()
                .map(postTag -> postTag.getTag().getTag())
                .collect(Collectors.toSet());
        Set<String> newTags = new HashSet<>(editBlogPostDTO.getTags());

        existingTags.stream()
                .filter(tag -> !newTags.contains(tag))
                .forEach(tag -> {
                    BlogTag findTag = blogTagRepository.findByTag(tag).orElse(null);
                    if (findTag != null)
                        blogPost.removeTag(findTag);
                });

        newTags.stream()
                .filter(tag -> !existingTags.contains(tag))
                .forEach(tag -> {
                    BlogTag blogTag = blogTagRepository.findByTag(tag)
                            .orElseGet(() -> blogTagRepository.save(new BlogTag(tag)));
                    blogPost.addTag(blogTag);
                });
    }
}

