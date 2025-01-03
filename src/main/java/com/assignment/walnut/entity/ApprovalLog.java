package com.assignment.walnut.entity;

import com.assignment.walnut.enums.ApprovalStatus;
import com.assignment.walnut.enums.VanCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "approval_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private ApprovalStatus status; // APPROVED, REJECTED 등 승인 상태 (nullable)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VanCode vanCode; // KICC, NICE, KSNET, SMARTRO 등 (nullable=false)

    @Column(nullable = false)
    private String type; // REQUEST 또는 RESPONSE

    @Column(nullable = true)
    private Double amount; // 거래 금액 (nullable)

    @Column(nullable = true, length = 3)
    private String currency; // 거래 통화 (nullable)

    @Column(nullable = true)
    private String approvalId; // 승인 요청 ID 또는 거래 고유 ID (nullable)

    @Column(nullable = true)
    private String approvalNumber; // 승인 번호 (nullable)

    @Lob
    @Column(nullable = true)
    private String reason; // 승인 거부 사유 또는 기타 이유 (nullable)

    @Column(nullable = false)
    private LocalDateTime timestamp; // 로그 생성 시각
}
