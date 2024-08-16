package com.alexdevstory.backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlogImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IMAGE_ID")
    private Long id;

    @Lob
    @Column(nullable = false)
    private byte[] imageData;

    @Column(nullable = false)
    private String fileName;

    private String contentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BLOGPOST_ID")
    private BlogPost blogPost;

    public void attachImageToBlogPost(BlogPost post) {
        this.blogPost = post;
        post.getImages().add(this);
    }

    public void detachImageToBlogPost() {
        this.blogPost = null;
    }

    @Builder
    public BlogImage(byte[] imageData, String fileName, String contentType, BlogPost blogPost) {
        this.imageData = imageData;
        this.fileName = fileName;
        this.contentType = contentType;
        attachImageToBlogPost(blogPost);
    }
}
