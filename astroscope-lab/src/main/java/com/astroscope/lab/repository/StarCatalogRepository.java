package com.astroscope.lab.repository;

import com.astroscope.lab.model.StarCatalogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StarCatalogRepository extends JpaRepository<StarCatalogEntry, Long> {

    @Query("SELECT s FROM StarCatalogEntry s WHERE lower(s.commonName) LIKE lower(concat('%', :q, '%')) " +
            "OR lower(s.designation) LIKE lower(concat('%', :q, '%')) ORDER BY s.magnitude ASC")
    List<StarCatalogEntry> searchSafe(@Param("q") String query);
}
