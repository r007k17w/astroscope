package com.astroscope.lab.repository;

import com.astroscope.lab.model.TelescopeImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TelescopeImageRepository extends JpaRepository<TelescopeImage, Long> {
    List<TelescopeImage> findByOwnerUsernameOrderByUploadedAtDesc(String username);
}
