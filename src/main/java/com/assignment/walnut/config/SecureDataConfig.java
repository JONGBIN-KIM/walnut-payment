package com.assignment.walnut.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecureDataConfig {

    @Value("${secure-data.secret-key-file}")
    private String secretKeyFile;

    @Value("${secure-data.whitelist-file}")
    private String whitelistFile;

    @Value("${secure-data.audit-log-file}")
    private String auditLogFile;

    public String getSecretKeyFile() {
        return secretKeyFile;
    }

    public String getWhitelistFile() {
        return whitelistFile;
    }

    public String getAuditLogFile() {
        return auditLogFile;
    }
}
