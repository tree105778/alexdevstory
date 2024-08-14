package com.alexdevstory.backend.repository;

import com.alexdevstory.backend.entity.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    @Query("SELECT bp FROM BlogPost bp " +
            "JOIN FETCH bp.tags pt " +
            "JOIN FETCH pt.tag t " +
            "where t.tag = :tagName")
    List<BlogPost> findBlogPostsByTagName(@Param("tagName") String tagName);
}
