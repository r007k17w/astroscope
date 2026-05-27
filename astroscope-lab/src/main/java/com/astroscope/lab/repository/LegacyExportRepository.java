package com.astroscope.lab.repository;

import com.astroscope.lab.model.User;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Legacy export queries retained for admin CSV tooling compatibility.
 */
@Repository
public class LegacyExportRepository {

    private final EntityManager entityManager;

    public LegacyExportRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @SuppressWarnings("unchecked")
    public List<User> findUsersForExport(String filterClause) {
        String sql = "SELECT * FROM users WHERE 1=1 " + filterClause + " ORDER BY username ASC";
        return entityManager.createNativeQuery(sql, User.class).getResultList();
    }
}
