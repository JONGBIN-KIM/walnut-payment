package com.assignment.walnut.service;

import com.assignment.walnut.dto.*;
import com.assignment.walnut.entity.AuditLog;
import com.assignment.walnut.entity.Card;
import com.assignment.walnut.entity.Transaction;
import com.assignment.walnut.enums.AcquireStatus;
import com.assignment.walnut.enums.ApprovalStatus;
import com.assignment.walnut.enums.TransactionStatus;
import com.assignment.walnut.repository.AuditLogRepository;
import com.assignment.walnut.repository.CardRepository;
import com.assignment.walnut.repository.TransactionRepository;
import com.assignment.walnut.util.ApprovalIso8583Converter;
import com.assignment.walnut.util.TokenIso8583Converter;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TransactionService {

    private final CardRepository cardRepository;
    private final TransactionRepository transactionRepository;
    private final AuditLogRepository auditLogRepository;

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    @Value("${van-system.url}")
    private String vanSystemUrl;

    @Value("${token-system.url}")
    private String tokenSystemUrl;

    public TransactionService(CardRepository cardRepository, TransactionRepository transactionRepository, AuditLogRepository auditLogRepository) {
        this.cardRepository = cardRepository;
        this.transactionRepository = transactionRepository;
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public TransactionCreateResponse createTransaction(TransactionCreateRequest request) {

        logAudit("TRANSACTION_REQUEST", "Processing transaction for orderId: " + request.getOrderNumber());

        boolean approveComplete = false;

        try {
            String refId = findRefId(request.getCardIdentifier());
            if (refId == null) {
                throw new IllegalArgumentException("Card identifier is not registered.");
            }
            logger.info("[확인] ref_id 유효성 확인  : " + refId);

            // Step 2: 토큰 발급 시스템에서 1회용 토큰 요청
            String token = requestTokenFromTokenSystem(refId);
            logger.info("[완료] 토큰발급 : " + token);

            // Step 3: 승인 시스템에 결제 요청
            boolean isApproved = requestApprovalFromVanSystem(token, request);
            logger.info("[완료] 승인완료 : " + request);
            approveComplete = true;

            // Step 4: 트랜잭션 저장
            Transaction transaction = saveTransaction(request, refId, isApproved);
            logAudit("TRANSACTION_RESPONSE", "Transaction result: " + (isApproved ? "APPROVED" : "FAILED"));

            return new TransactionCreateResponse(
                    transaction.getTransactionId(),
                    transaction.getStatus().name(),
                    transaction.getStoreOrderNumber(),
                    transaction.getApprovedAt(),
                    isApproved ? LocalDateTime.now() : null
            );

        } catch (Exception e){
            if(approveComplete){
                // 디테일을 높이면 승인취소요청 구현필요
            }else{
                throw new RuntimeException("Error!", e);
            }
        }
        return null;
    }

    public List<TransactionResponse> getRecentTransactions(String storeId) {
        List<Transaction> transactions;

        if (storeId != null) {
            transactions = transactionRepository.findTop10ByStoreIdOrderByCreatedAtDesc(storeId);
        } else {
            transactions = transactionRepository.findTop10ByOrderByCreatedAtDesc();
        }

        return transactions.stream()
                .map(transaction -> TransactionResponse.builder()
                        .transactionId(transaction.getTransactionId())
                        .storeId(transaction.getStoreId())
                        .amount(transaction.getAmount())
                        .currency(transaction.getCurrency())
                        .status(transaction.getStatus().name())
                        .createdAt(transaction.getCreatedAt())
                        .approvedAt(transaction.getApprovedAt())
                        .build())
                .toList();
    }





    private String findRefId(String cardIdentifier) {
        Optional<Card> card = cardRepository.findByCardIdentifier(cardIdentifier);
        return card.map(Card::getRefId).orElse(null);
    }

    private String requestTokenFromTokenSystem(String refId) {
        String tokenRequestUrl = tokenSystemUrl + "/tokens/issue";

        RestTemplate restTemplate = new RestTemplate();

        // ISO 8583 전문 데이터 생성
        String payload = TokenIso8583Converter.toIso8583Payload(refId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.add("X-Original-URI", "/api/tokens/issue");

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        // API 호출 및 응답 처리
        try {
            logger.info("[요청] 일회용 토큰발급요청 - ref_id : "+refId);
            String responsePayload = restTemplate.postForObject(tokenRequestUrl, entity, String.class);
            logger.info("[완료] responsePayload : "+responsePayload);

            TokenIssueResponse response = TokenIso8583Converter.fromIso8583Payload(responsePayload);
            logger.info("[완료] response 결과 : "+response);
            System.out.println(response);

            if (response.getToken() == null || response.getToken().trim().isEmpty()) {
                throw new IllegalStateException("Token system failed to return a valid token.");
            }
            logger.info("[성공] token : "+response.getToken().trim());

            return response.getToken().trim();
        } catch (Exception e) {
            throw new IllegalStateException("Error communicating with the token system: " + e.getMessage(), e);
        }
    }

    private Boolean requestApprovalFromVanSystem(String token, TransactionCreateRequest request) {
        RestTemplate restTemplate = new RestTemplate();

        ApprovalRequest approvalRequest = ApprovalRequest.builder()
                .token(token)
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .approvalId(request.getOrderNumber())
                .build();

        String payload = ApprovalIso8583Converter.toIso8583Payload(approvalRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.add("X-Original-URI", "/api/approve");

        HttpEntity<String> entity = new HttpEntity<>(payload, headers);

        try {
            logger.info("[승인요청] token : "+token);
            logger.info("[승인요청] amount : "+request.getAmount());
            String responsePayload = restTemplate.postForObject(vanSystemUrl, entity, String.class);
            logger.info("[승인응답] payload : "+responsePayload);

            ApprovalResponse approvalResponse = ApprovalIso8583Converter.fromIso8583Payload(responsePayload);

            logger.info("[승인결과] result : "+approvalResponse.getStatus());
            return approvalResponse.getStatus().equals(ApprovalStatus.APPROVED);
        } catch (Exception e) {
            throw new IllegalStateException("Error communicating with VAN system: " + e.getMessage(), e);
        }
    }

    // ISO 8583 전문 포맷으로 변환
    private String toIso8583Payload(ApprovalRequest request) {
        return String.format(
                "%-32s%-12.2f%-3s%-20s",
                request.getToken(),
                request.getAmount(),
                request.getCurrency(),
                request.getApprovalId()
        );
    }

    // ISO 8583 전문을 ApprovalResponse로 변환
    private ApprovalResponse fromIso8583Payload(String payload) {
        String status = payload.substring(0, 8).trim();
        String approvalId = payload.substring(8, 28).trim();
        String reason = payload.substring(28, 128).trim();
        String timestamp = payload.substring(128, 148).trim();
        String approvalNumber = payload.substring(148, 168).trim();
        String acquireStatus = payload.substring(168).trim();

        return ApprovalResponse.builder()
                .status(ApprovalStatus.valueOf(status))
                .approvalId(approvalId)
                .reason(reason)
                .timestamp(LocalDateTime.parse(timestamp))
                .approvalNumber(approvalNumber)
                .acquireStatus(AcquireStatus.valueOf(acquireStatus))
                .build();
    }


    private Transaction saveTransaction(TransactionCreateRequest request, String refId, boolean isApproved) {
        return transactionRepository.save(
                Transaction.builder()
                        .transactionId(UUID.randomUUID().toString())
                        .refId(refId)
                        .amount(request.getAmount())
                        .currency(request.getCurrency())
                        .status(isApproved ? TransactionStatus.DONE : TransactionStatus.ABORTED)
                        .paymentType(request.getPaymentType())
                        .installmentMonths(request.getInstallmentMonths())
                        .isInterestFree(true)
                        .storeId(request.getStoreId())
                        .storeOrderNumber(request.getOrderNumber())
                        .storeOrderName(request.getOrderNumber())
                        .createdAt(LocalDateTime.now())
                        .requestedAt(LocalDateTime.now())
                        .approvedAt(isApproved ? LocalDateTime.now() : null)
                        .build()
        );
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
