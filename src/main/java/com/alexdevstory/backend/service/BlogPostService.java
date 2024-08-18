package com.alexdevstory.backend.service;

import com.alexdevstory.backend.dto.BlogImageDTO;
import com.alexdevstory.backend.dto.BlogPostDTO;
import com.alexdevstory.backend.entity.BlogImage;
import com.alexdevstory.backend.entity.BlogPost;
import com.alexdevstory.backend.entity.BlogTag;
import com.alexdevstory.backend.entity.PostTag;
import com.alexdevstory.backend.exception.BlogPostNotFoundException;
import com.alexdevstory.backend.repository.BlogImageRepository;
import com.alexdevstory.backend.repository.BlogPostRepository;
import com.alexdevstory.backend.repository.BlogTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogPostService {
    private final BlogPostRepository blogPostRepository;
    private final BlogTagRepository blogTagRepository;
    private final BlogImageRepository blogImageRepository;

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

    /*@Transactional
    public BlogPostDTO editBlogPost(String title, BlogPostDTO editBlogPostDTO) {
        BlogPost blogPost = blogPostRepository.findByTitle(title).orElseThrow(
                () -> new BlogPostNotFoundException("No Blog Post title:" + title));

        blogPost.editBlogPost(editBlogPostDTO.getTitle(), editBlogPostDTO.getContent());
    }*/

    public BlogPostDTO getBlogPost(String title) {
        BlogPost blogPost = blogPostRepository.findByTitle(title).orElseThrow(
                () -> new BlogPostNotFoundException("No Blog Post title:" + title));

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

        BlogPostDTO blogPostDTO = BlogPostDTO.builder()
                .title(blogPost.getTitle())
                .content(blogPost.getContent())
                .tags(tagList)
                .images(blogImageDTOList)
                .build();
        return blogPostDTO;
    }
    public boolean deleteBlogPost(String title) {
        BlogPost findPost = blogPostRepository.findByTitle(title).orElseThrow(
                () -> new BlogPostNotFoundException("No Blog Post title:" + title));
        blogPostRepository.delete(findPost);

        return true;
    }
}

