package com.assignment.walnut.repository;

import com.assignment.walnut.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByCardIdentifier(String cardIdentifier);

    List<Card> findTop10ByOrderByCreatedAtDesc();
}
