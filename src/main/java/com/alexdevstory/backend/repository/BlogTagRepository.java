package com.alexdevstory.backend.repository;

import com.alexdevstory.backend.entity.BlogTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlogTagRepository extends JpaRepository<BlogTag, Long> {
    Optional<BlogTag> findByTag(String tag);
}
