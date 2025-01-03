package com.assignment.walnut.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {
    private String transactionId;
    private String storeId;
    private Double amount;
    private String currency;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
}
