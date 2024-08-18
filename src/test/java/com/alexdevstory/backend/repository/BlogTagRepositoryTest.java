package com.alexdevstory.backend.repository;

import com.alexdevstory.backend.entity.BlogTag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BlogTagRepositoryTest {
    @Autowired
    BlogTagRepository blogTagRepository;
    @Test
    void blogTagRepositoryTest() {
        BlogTag firstTag = blogTagRepository.save(new BlogTag("first tag"));
        BlogTag secondTag = blogTagRepository.save(new BlogTag("second tag"));

        //블로그 테그 리포지토리에서 가져온 BlogTag가 저장한 원본과 동일한 지 검증
        Optional<BlogTag> findFirstTag = blogTagRepository.findByTag(firstTag.getTag());
        assertThat(findFirstTag.get().getTag()).isEqualTo(firstTag.getTag());

        Optional<BlogTag> findSecondTag = blogTagRepository.findByTag(secondTag.getTag());
        assertThat(findSecondTag.get().getTag()).isEqualTo(secondTag.getTag());
    }
}