package com.assignment.walnut.controller;

import com.assignment.walnut.dto.CardRegistrationRequest;
import com.assignment.walnut.dto.CardRegistrationResponse;
import com.assignment.walnut.entity.Card;
import com.assignment.walnut.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/register")
    public ResponseEntity<CardRegistrationResponse> registerCard(@Validated @RequestBody CardRegistrationRequest cardRequest) {
        CardRegistrationResponse response = cardService.registerCard(cardRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<Card>> getCards() {
        List<Card> cards = cardService.getFindTop10Cards();
        return ResponseEntity.ok(cards);
    }
}
