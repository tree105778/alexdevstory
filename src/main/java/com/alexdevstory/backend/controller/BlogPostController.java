package com.alexdevstory.backend.controller;

import com.alexdevstory.backend.dto.*;
import com.alexdevstory.backend.exception.ImageProcessingException;
import com.alexdevstory.backend.service.BlogPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class BlogPostController {
    private final BlogPostService blogPostService;

    @GetMapping
    public ResponseEntity<BlogPostSummaryAndPageDTO> getAllBlogPosts(Pageable pageable) {
        BlogPostSummaryAndPageDTO blogPosts = blogPostService.getAllBlogPostSummary(pageable);
        return ResponseEntity.ok(blogPosts);
    }

    @GetMapping("/{title}")
    public ResponseEntity<BlogPostDTO> getBlogPost(@PathVariable String title) {
        BlogPostDTO blogPostDTO = blogPostService.getBlogPost(title);
        return ResponseEntity.ok(blogPostDTO);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<BlogPostDTO> createBlogPost(
            @ModelAttribute BlogPostFormDTO blogPostFormDTO,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {
        BlogPostDTO blogPostDTO = BlogPostDTO.builder()
                .title(blogPostFormDTO.getTitle())
                .content(blogPostFormDTO.getContent())
                .tags(blogPostFormDTO.getTags())
                .build();
        List<BlogImageDTO> blogImageDTOList = imageFiles.stream()
                .map(file -> {
                    try {
                        return BlogImageDTO.builder()
                                .imageData(file.getBytes())
                                .fileName(file.getOriginalFilename())
                                .contentType(file.getContentType())
                                .build();
                    } catch (IOException e) {
                        throw new ImageProcessingException("Error while processing image file", e);
                    }
                }).collect(Collectors.toList());

        blogPostDTO.setImages(blogImageDTOList);
        blogPostService.saveBlogPost(blogPostDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(blogPostDTO);
    }

    @GetMapping("/search")
    public ResponseEntity<BlogPostSummaryAndPageDTO> searchBlogPosts(
            @RequestParam String keyword, Pageable pageable) {
        Page<BlogPostSummaryDTO> blogPosts
                = blogPostService.getBlogPostsBySearchCond(keyword, pageable);

        BlogPostSummaryAndPageDTO blogPostSummaryAndPageDTO = BlogPostSummaryAndPageDTO.builder()
                .totalPage(blogPosts.getTotalPages())
                .currentPage(blogPosts.getNumber())
                .totalElements(blogPosts.getTotalElements())
                .numberOfCurrentElements(blogPosts.getNumberOfElements())
                .summaries(blogPosts.getContent())
                .build();

        return ResponseEntity.ok(blogPostSummaryAndPageDTO);
    }

    @GetMapping("/tags")
    public ResponseEntity<BlogPostSummaryAndPageDTO> searchBlogPostsByTags(
            @RequestParam List<String> tags, Pageable pageable) {
        BlogPostSummaryAndPageDTO blogPostsByTags = blogPostService.getBlogPostsByTags(tags, pageable);
        return ResponseEntity.ok(blogPostsByTags);
    }

    @DeleteMapping("/{title}")
    public ResponseEntity<Void> deleteBlogPost(@PathVariable String title) {
        if (blogPostService.deleteBlogPost(title)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
