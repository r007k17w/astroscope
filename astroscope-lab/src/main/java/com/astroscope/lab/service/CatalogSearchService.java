package com.astroscope.lab.service;

import com.astroscope.lab.model.StarCatalogEntry;
import com.astroscope.lab.repository.StarCatalogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogSearchService {

    private final StarCatalogRepository starCatalogRepository;

    public CatalogSearchService(StarCatalogRepository starCatalogRepository) {
        this.starCatalogRepository = starCatalogRepository;
    }

    public List<StarCatalogEntry> search(String query, String rankingMode) {
        if (query == null || query.isBlank()) {
            return starCatalogRepository.findAll();
        }
        return starCatalogRepository.searchSafe(query.trim());
    }
}
