package com.alexdevstory.backend.repository;

import com.alexdevstory.backend.entity.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    @Query("SELECT DISTINCT bp FROM BlogPost bp " +
            "JOIN FETCH bp.tags pt " +
            "JOIN FETCH pt.tag t " +
            "where t.tag = :tagName")
    List<BlogPost> findBlogPostsByTagName(@Param("tagName") String tagName);

    @Query("SELECT bp FROM BlogPost bp " +
            "JOIN FETCH bp.tags pt " +
            "JOIN FETCH pt.tag t " +
            "where t.tag in :tags")
    Page<BlogPost> findBlogPostsByTags(@Param("tags") List<String> tags, Pageable pageable);

    @EntityGraph(attributePaths = {"tags.tag", "images"})
    Optional<BlogPost> findByTitle(String title);
}
