package com.assignment.walnut.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApprovalRequest {
    private String approvalId;
    private String token;
    private Double amount;
    private String currency;
}
