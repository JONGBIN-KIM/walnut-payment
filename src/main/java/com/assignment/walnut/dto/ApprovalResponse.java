package com.assignment.walnut.dto;

import com.assignment.walnut.enums.AcquireStatus;
import com.assignment.walnut.enums.ApprovalStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApprovalResponse {
    private ApprovalStatus status;
    private String approvalId;
    private String reason; // 실패 시 실패 사유
    private Double amount;
    private String currency;
    private LocalDateTime timestamp;
    private String approvalNumber; // 승인 번호
    private AcquireStatus acquireStatus; // 매입 상태
}
