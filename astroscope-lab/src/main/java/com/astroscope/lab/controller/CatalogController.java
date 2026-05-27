package com.astroscope.lab.controller;

import com.astroscope.lab.platform.content.CatalogViewComposer;
import com.astroscope.lab.service.CatalogSearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CatalogController {

    private final CatalogSearchService catalogSearchService;
    private final CatalogViewComposer catalogViewComposer;

    public CatalogController(CatalogSearchService catalogSearchService,
                             CatalogViewComposer catalogViewComposer) {
        this.catalogSearchService = catalogSearchService;
        this.catalogViewComposer = catalogViewComposer;
    }

    @GetMapping("/catalog")
    public String search(@RequestParam(required = false) String q,
                         @RequestParam(defaultValue = "mag") String ranking,
                         Model model) {
        model.addAttribute("query", q == null ? "" : q);
        model.addAttribute("ranking", ranking);
        model.addAttribute("results", catalogSearchService.search(q, ranking));
        model.addAttribute("pageBootScript", catalogViewComposer.composeBootScript(q, ranking));
        return "catalog";
    }
}
