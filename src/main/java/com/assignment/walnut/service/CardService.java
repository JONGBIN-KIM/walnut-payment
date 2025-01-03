package com.assignment.walnut.service;

import com.assignment.walnut.dto.CardRegistrationRequest;
import com.assignment.walnut.dto.CardRegistrationResponse;
import com.assignment.walnut.dto.TokenResponse;
import com.assignment.walnut.entity.AuditLog;
import com.assignment.walnut.entity.Card;
import com.assignment.walnut.repository.AuditLogRepository;
import com.assignment.walnut.repository.CardRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final AuditLogRepository auditLogRepository;

    private static final Logger logger = LoggerFactory.getLogger(CardService.class);


    @Value("${token-system.url}")
    private String tokenSystemUrl;

    public CardService(CardRepository cardRepository, AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
        this.cardRepository = cardRepository;
    }

    public List<Card> getFindTop10Cards() {
        return cardRepository.findTop10ByOrderByCreatedAtDesc();
    }

    public CardRegistrationResponse registerCard(CardRegistrationRequest request) {

        logAudit("CARD_REGISTER_REQUEST", "Received card registration request for storeId: " + request.getStoreId());

        logger.info("[시작]토큰 발급 시스템에 카드 등록 요청");
        String refId = registerCardWithTokenSystem(request);
        logger.info("[완료]토큰 발급 시스템에 카드 등록 요청");

        String cardIdentifier = UUID.randomUUID().toString();

        Card card = Card.builder()
                .ci(request.getCi())
                .refId(refId)
                .cardIdentifier(cardIdentifier)
                .createdAt(LocalDateTime.now())
                .build();
        cardRepository.save(card);

        logAudit("CARD_REGISTER_SUCCESS", "Card registered successfully with refId: " + refId);

        return new CardRegistrationResponse(cardIdentifier, request.getRequestUniqueId());
    }

    private String registerCardWithTokenSystem(CardRegistrationRequest request) {
        String cardRegistrationUrl = tokenSystemUrl + "/cards";

        RestTemplate restTemplate = new RestTemplate();

        // ISO 8583 전문 데이터 생성
        String payload = String.format("%-20s%-256s%-4s",
                request.getCi(),
                request.getEncryptedCardInfo(),
                request.getStoreId());

        // 헤더 설정 (X-Original-URI 포함)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.add("X-Original-URI", "/api/cards");

        // 요청 생성
        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        // API 호출
        try {
            TokenResponse response = restTemplate.postForObject(cardRegistrationUrl, entity, TokenResponse.class);

            if (response == null || response.getRefId() == null || response.getRefId().trim().isEmpty()) {
                logger.error("Token system failed to register card: " + (response != null ? response.getMessage() : "Unknown error"));
                throw new IllegalStateException("Token system failed to register card");
            }

            return response.getRefId().trim();
        } catch (Exception e) {
            throw new IllegalStateException("Error communicating with the token system: " + e.getMessage(), e);
        }
    }

    private void logAudit(String action, String details) {
        AuditLog auditLog = AuditLog.builder()
                .action(action)
                .details(details)
                .createdAt(LocalDateTime.now())
                .createdBy("SYSTEM")
                .build();
        auditLogRepository.save(auditLog);
    }
}
