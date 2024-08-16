package com.alexdevstory.backend.repository;

import com.alexdevstory.backend.entity.BlogImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BlogImageRepository extends JpaRepository<BlogImage, Long> {
    @Query("select bm from BlogImage bm " +
            "join fetch bm.blogPost bp " +
            "where bp.title = :title")
    List<BlogImage> findBlogImagesByPostTitle(@Param("title") String blogTitle);
}
