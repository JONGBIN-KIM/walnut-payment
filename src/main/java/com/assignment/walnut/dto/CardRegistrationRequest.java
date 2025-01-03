package com.assignment.walnut.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CardRegistrationRequest {

    @NotBlank
    private String requestUniqueId;

    @NotBlank
    private String ci;

    @NotBlank
    private String encryptedCardInfo;

    @NotBlank
    private String storeId;

}