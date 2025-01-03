package com.assignment.walnut.dto;

import com.assignment.walnut.entity.PaymentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransactionCreateRequest {

    @NotBlank
    private String cardIdentifier;

    @NotBlank
    private String orderNumber;

    @NotBlank
    private String storeId;

    @NotNull
    private Double amount;

    @NotBlank
    private String currency;

    @NotNull
    private PaymentType paymentType;

    private Integer installmentMonths = 0;

    private Boolean isInterestFree = false;
}
