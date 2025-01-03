package com.assignment.walnut.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CardRegistrationResponse {
    private String cardIdentifier;  // 사용자 식별 키
    private String requestUniqueId; // 요청 고유 번호
}
