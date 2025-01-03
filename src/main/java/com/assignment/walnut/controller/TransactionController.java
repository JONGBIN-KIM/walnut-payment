package com.assignment.walnut.controller;

import com.assignment.walnut.dto.TransactionCreateRequest;
import com.assignment.walnut.dto.TransactionCreateResponse;
import com.assignment.walnut.dto.TransactionResponse;
import com.assignment.walnut.service.TransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionCreateResponse> createTransaction(@Validated @RequestBody TransactionCreateRequest request) {
        TransactionCreateResponse response = transactionService.createTransaction(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @RequestParam(required = false) String storeId
    ) {
        List<TransactionResponse> transactions = transactionService.getRecentTransactions(storeId);
        return ResponseEntity.ok(transactions);
    }

}
