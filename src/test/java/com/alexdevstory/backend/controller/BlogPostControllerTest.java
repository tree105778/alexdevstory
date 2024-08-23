package com.alexdevstory.backend.controller;

import com.alexdevstory.backend.service.BlogPostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BlogPostControllerTest {

    private final String baseUrl = "/api/v1/posts";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BlogPostService blogPostService;

    @Test
    void getAllBlogPosts() throws Exception {
        final String url = baseUrl;

        mockMvc.perform(get(url)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void createBlogPost() {
    }

    @Test
    void getBlogPost() {
    }

    @Test
    void updateBlogPost() {
    }

    @Test
    void deleteBlogPost() {
    }

    @Test
    void searchBlogPosts() {
    }

    @Test
    void searchBlogPostsByTags() {
    }
}