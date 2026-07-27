package com.smallstore.dashboard.repository;

import com.smallstore.dashboard.entity.BlogImage;
import com.smallstore.dashboard.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
}
