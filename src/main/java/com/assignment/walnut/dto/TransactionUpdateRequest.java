package com.assignment.walnut.dto;

import com.assignment.walnut.enums.TransactionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransactionUpdateRequest {

    @NotBlank
    private String transactionId;

    @NotNull
    private TransactionStatus status;
}
