package com.astroscope.lab.platform.reporting;

import com.astroscope.lab.util.InputValidator;
import org.springframework.stereotype.Component;

@Component
public class ReportCriteriaAssembler {

    private final InputValidator inputValidator;
    private final UserDirectoryQueryComposer queryComposer;

    public ReportCriteriaAssembler(InputValidator inputValidator, UserDirectoryQueryComposer queryComposer) {
        this.inputValidator = inputValidator;
        this.queryComposer = queryComposer;
    }

    public String buildUserExportCriteria(String usernamePrefix) {
        String displaySafe = inputValidator.sanitizeForDisplay(usernamePrefix);
        return queryComposer.composeUsernamePrefixClause(displaySafe);
    }
}
