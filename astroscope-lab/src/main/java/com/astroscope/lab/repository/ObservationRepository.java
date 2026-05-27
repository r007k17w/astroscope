package com.astroscope.lab.repository;

import com.astroscope.lab.model.Observation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObservationRepository extends JpaRepository<Observation, Long> {
    List<Observation> findByIsPrivateFalseOrderByCreatedAtDesc();
    List<Observation> findByAuthorUsernameOrderByCreatedAtDesc(String username);
}
