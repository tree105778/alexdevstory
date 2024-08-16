package com.alexdevstory.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlogPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BLOGPOST_ID")
    private Long id;

    @Column(updatable = false)
    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @OneToMany(mappedBy = "blogPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BlogImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "blogPost", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PostTag> tags = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdDate = now;
        this.lastModifiedDate = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = LocalDateTime.now();
    }

    public void addImage(BlogImage image) {
        image.attachImageToBlogPost(this);
        images.add(image);
    }

    public void removeImage(BlogImage image) {
        images.remove(image);
        image.detachImageToBlogPost();
    }

    public void addTag(BlogTag tag) {
        PostTag postTag = new PostTag(this, tag);
        tags.add(postTag);
    }

    public void removeTag(BlogTag tag) {
        tags.removeIf(postTag -> postTag.getTag().equals(tag));
    }

    public BlogPost editBlogPost(String title, String content) {
        if (title != null)
            this.title = title;
        if (content != null)
            this.content = content;

        return this;
    }

    @Builder(toBuilder = true)
    public BlogPost(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
