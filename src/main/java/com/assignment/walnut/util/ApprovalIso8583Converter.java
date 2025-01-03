package com.assignment.walnut.util;

import com.assignment.walnut.dto.ApprovalRequest;
import com.assignment.walnut.dto.ApprovalResponse;
import com.assignment.walnut.enums.ApprovalStatus;

import java.time.LocalDateTime;

public class ApprovalIso8583Converter {

    // ApprovalRequest를 ISO 8583 전문으로 변환
    public static String toIso8583Payload(ApprovalRequest request) {
        return String.format(
                "%-64s%-3s%-19s%-12.2f",
                padRight(request.getToken(),64),
                padRight(request.getCurrency(),3),
                padRight(request.getApprovalId(),20),
                request.getAmount()
        );
    }

    // ISO 8583 전문을 ApprovalResponse로 변환
    public static ApprovalResponse fromIso8583Payload(String payload) {
        try {
            System.out.println("#############");
            System.out.println(payload);

            String status = payload.length() >= 8 ? payload.substring(0, 8).trim() : "REJECTED";
            String approvalId = payload.length() >= 28 ? payload.substring(8, 28).trim() : "UNKNOWN";
            String reason = payload.length() >= 128 ? payload.substring(28, 128).trim() : "No reason provided";
            String timestamp = payload.length() >= 148 ? payload.substring(128, 148).trim() : LocalDateTime.now().toString();
            String approvalNumber = payload.length() >= 168 ? payload.substring(148, 168).trim() : "UNKNOWN";

            return ApprovalResponse.builder()
                    .status(ApprovalStatus.valueOf(status))
                    .approvalId(approvalId)
                    .reason(reason)
                    .timestamp(LocalDateTime.parse(timestamp))
                    .approvalNumber(approvalNumber)
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException("Error parsing ISO 8583 payload: " + e.getMessage(), e);
        }
    }


    private static String padRight(String value, int length) {
        if (value == null) {
            value = "";
        }
        if (value.length() > length) {
            return value.substring(0, length);
        }
        return String.format("%-" + length + "s", value);
    }
}
