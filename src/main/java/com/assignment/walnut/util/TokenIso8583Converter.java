package com.assignment.walnut.util;

import com.assignment.walnut.dto.TokenIssueResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TokenIso8583Converter {

    public static String toIso8583Payload(String refId) {
        return String.format("%-20s", refId);
    }

    public static TokenIssueResponse fromIso8583Payload(String payload) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(payload, TokenIssueResponse.class);
        } catch (Exception e) {
            throw new IllegalStateException("Error parsing ISO 8583 payload: " + e.getMessage(), e);
        }
    }
}
