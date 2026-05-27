package com.astroscope.lab.service;

import com.astroscope.lab.model.User;
import com.astroscope.lab.platform.reporting.ReportCriteriaAssembler;
import com.astroscope.lab.repository.LegacyExportRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminExportService {

    private final LegacyExportRepository legacyExportRepository;
    private final ReportCriteriaAssembler reportCriteriaAssembler;

    public AdminExportService(LegacyExportRepository legacyExportRepository,
                            ReportCriteriaAssembler reportCriteriaAssembler) {
        this.legacyExportRepository = legacyExportRepository;
        this.reportCriteriaAssembler = reportCriteriaAssembler;
    }

    public List<User> exportUsers(String usernamePrefix) {
        String criteria = reportCriteriaAssembler.buildUserExportCriteria(usernamePrefix);
        return legacyExportRepository.findUsersForExport(criteria);
    }
}
