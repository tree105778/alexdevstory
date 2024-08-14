package com.alexdevstory.backend.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @OneToMany(mappedBy = "blogPost", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PostTag> tags = new ArrayList<>();


    public void addTag(BlogTag tag) {
        PostTag postTag = new PostTag(this, tag);
        tags.add(postTag);
    }

    public void removeTag(BlogTag tag) {
        tags.removeIf(postTag -> postTag.getTag().equals(tag));
    }

    @Builder
    public BlogPost(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
