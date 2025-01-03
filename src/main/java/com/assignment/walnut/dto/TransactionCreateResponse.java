package com.assignment.walnut.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class TransactionCreateResponse {

    private String transactionId;
    private String status;
    private String orderNumber;
    private LocalDateTime approvedAt;
    private LocalDateTime completedAt;
}
