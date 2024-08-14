package com.alexdevstory.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class PostTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BLOGPOST_TAG_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BLOGPOST_ID")
    private BlogPost blogPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TAG_ID")
    private BlogTag tag;

    public PostTag(BlogPost blogPost, BlogTag tag) {
        this.blogPost = blogPost;
        this.tag = tag;
    }
}
