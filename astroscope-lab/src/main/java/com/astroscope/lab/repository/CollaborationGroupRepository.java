package com.astroscope.lab.repository;

import com.astroscope.lab.model.CollaborationGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CollaborationGroupRepository extends JpaRepository<CollaborationGroup, Long> {
    Optional<CollaborationGroup> findBySlug(String slug);
}
