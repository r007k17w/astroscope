package com.astroscope.lab.repository;

import com.astroscope.lab.model.PaperShare;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaperShareRepository extends JpaRepository<PaperShare, Long> {
    Optional<PaperShare> findByShareToken(String shareToken);
}
