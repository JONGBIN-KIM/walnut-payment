package com.assignment.walnut;

import com.assignment.walnut.dto.CardRegistrationRequest;
import com.assignment.walnut.dto.CardRegistrationResponse;
import com.assignment.walnut.dto.TransactionCreateRequest;
import com.assignment.walnut.dto.TransactionCreateResponse;
import com.assignment.walnut.entity.PaymentType;
import com.assignment.walnut.service.CardService;
import com.assignment.walnut.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TransactionCreateTest {

    @Autowired
    private CardService cardService;

    @Autowired
    private TransactionService transactionService;

    @Test
    void testCardRegistrationAndTransaction() {

        // Step 1: 카드 등록
        CardRegistrationRequest cardRequest = new CardRegistrationRequest();
        cardRequest.setCi("CI123456789");
        cardRequest.setEncryptedCardInfo("ENC1234567890123456");
        cardRequest.setStoreId("STORE123");
        cardRequest.setRequestUniqueId("REQ123456");

        CardRegistrationResponse cardResponse = cardService.registerCard(cardRequest);

        assertThat(cardResponse).isNotNull();
        assertThat(cardResponse.getCardIdentifier()).isNotBlank();
        assertThat(cardResponse.getRequestUniqueId()).isEqualTo("REQ123456");

        // Step 2: 거래 요청
        TransactionCreateRequest transactionRequest = new TransactionCreateRequest();
        transactionRequest.setCardIdentifier(cardResponse.getCardIdentifier());
        transactionRequest.setOrderNumber("ORDER123456");
        transactionRequest.setStoreId("STORE123");
        transactionRequest.setAmount(10000.0);
        transactionRequest.setCurrency("KRW");
        transactionRequest.setPaymentType(PaymentType.CARD);
        transactionRequest.setInstallmentMonths(0);
        transactionRequest.setIsInterestFree(false);

        TransactionCreateResponse transactionResponse = transactionService.createTransaction(transactionRequest);

        assertThat(transactionResponse).isNotNull();
        assertThat(transactionResponse.getTransactionId()).isNotBlank();
        assertThat(transactionResponse.getStatus()).isEqualTo("DONE");
        assertThat(transactionResponse.getOrderNumber()).isEqualTo("ORDER123456");
        assertThat(transactionResponse.getApprovedAt()).isNotNull();
        assertThat(transactionResponse.getCompletedAt()).isNotNull();
    }
}
