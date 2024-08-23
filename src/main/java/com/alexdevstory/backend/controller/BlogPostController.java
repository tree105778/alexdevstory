package com.alexdevstory.backend.controller;

import com.alexdevstory.backend.dto.BlogPostDTO;
import com.alexdevstory.backend.dto.BlogPostFormDTO;
import com.alexdevstory.backend.dto.BlogPostSummaryAndPageDTO;
import com.alexdevstory.backend.service.BlogPostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class BlogPostController {
    private final BlogPostService blogPostService;

    @GetMapping
    public ResponseEntity<BlogPostSummaryAndPageDTO> getAllBlogPosts(Pageable pageable) {
        log.info("Fetching all blog posts with pagination: {}", pageable);
        BlogPostSummaryAndPageDTO blogPosts = blogPostService.getAllBlogPostSummary(pageable);
        log.info("Successfully fetched {} blog posts", blogPosts.getSummaries().size());
        return ResponseEntity.ok(blogPosts);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<BlogPostDTO> createBlogPost(
            @ModelAttribute BlogPostFormDTO blogPostFormDTO,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {
        log.info("creating new blog post: {}", blogPostFormDTO.getTitle());
        BlogPostDTO blogPostDTO = blogPostService.convertToBlogPostDTO(blogPostFormDTO, imageFiles);
        blogPostService.saveBlogPost(blogPostDTO);
        log.info("successfully created blog post: {}", blogPostFormDTO.getTitle());

        return ResponseEntity.status(HttpStatus.CREATED).body(blogPostDTO);
    }

    @GetMapping("/{title}")
    public ResponseEntity<BlogPostDTO> getBlogPost(@PathVariable String title) {
        log.info("Fetching blog post: {}", title);
        BlogPostDTO blogPostDTO = blogPostService.getBlogPost(title);
        log.info("successfully fetched blog post: {}", title);

        return ResponseEntity.ok(blogPostDTO);
    }

    @PutMapping(value = "/{title}", consumes = {"multipart/form-data"})
    public ResponseEntity<BlogPostDTO> updateBlogPost(
            @PathVariable String title, @RequestParam BlogPostFormDTO blogPostFormDTO,
            @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {
        log.info("Updating blog post: {}", title);
        BlogPostDTO blogPostDTO = blogPostService.convertToBlogPostDTO(blogPostFormDTO, imageFiles);
        BlogPostDTO updatedBlogPost = blogPostService.editBlogPost(title, blogPostDTO);
        log.info("Successfully updating blog post: {} to {}", title, updatedBlogPost.getTitle());

        return ResponseEntity.ok(updatedBlogPost);
    }

    @DeleteMapping("/{title}")
    public ResponseEntity<Void> deleteBlogPost(@PathVariable String title) {
        log.info("Deleting blog post: {}", title);
        blogPostService.deleteBlogPost(title);
        log.info("Successfully deleting blog post: {}", title);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<BlogPostSummaryAndPageDTO> searchBlogPosts(
            @RequestParam String keyword, Pageable pageable) {
        log.info("Searching Blog post for keyword: {} with pageable: {}", keyword, pageable);
        BlogPostSummaryAndPageDTO blogPostSummaryAndPageDTO
                = blogPostService.getBlogPostsBySearchCond(keyword, pageable);
        log.info("Successfully Found {} blog posts for keyword: {}",
                blogPostSummaryAndPageDTO.getTotalElements(), keyword);

        return ResponseEntity.ok(blogPostSummaryAndPageDTO);
    }

    @GetMapping("/q")
    public ResponseEntity<BlogPostSummaryAndPageDTO> searchBlogPostsByTags(
            @RequestParam List<String> tags, Pageable pageable) {
        log.info("Searching Blog post with tags: {}", tags);
        BlogPostSummaryAndPageDTO blogPostsByTags = blogPostService.getBlogPostsByTags(tags, pageable);
        log.info("Successfully Found {} blog posts for tags: {}",
                blogPostsByTags.getTotalElements(), tags);

        return ResponseEntity.ok(blogPostsByTags);
    }
}
