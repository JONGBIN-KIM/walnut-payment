package com.assignment.walnut.entity;

import com.assignment.walnut.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id", nullable = false, unique = true, length = 64)
    private String transactionId;

    @Column(name = "ref_id", nullable = false, length = 64)
    private String refId;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false, length = 8)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private TransactionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PaymentType paymentType;

    @Column(name = "installment_months", nullable = false)
    private Integer installmentMonths;

    @Column(name = "is_interest_free")
    private Boolean isInterestFree;

    @Column(name = "store_id", nullable = false)
    private String storeId;

    @Column(name = "store_order_number", nullable = false)
    private String storeOrderNumber;

    @Column(name = "store_order_name", nullable = false)
    private String storeOrderName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
}
