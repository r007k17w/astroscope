package com.astroscope.lab.repository;

import com.astroscope.lab.model.ModeratorDelegation;
import com.astroscope.lab.model.User;
import com.astroscope.lab.model.CollaborationGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ModeratorDelegationRepository extends JpaRepository<ModeratorDelegation, Long> {
    List<ModeratorDelegation> findByDelegateAndActiveTrue(User delegate);
    Optional<ModeratorDelegation> findByDelegateAndGroupAndActiveTrue(User delegate, CollaborationGroup group);
}
