package com.manoj.risk.service;

import com.manoj.risk.dto.TransactionRequest;
import com.manoj.risk.model.RiskAssessment;
import com.manoj.risk.model.Transaction;
import com.manoj.risk.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
  private final TransactionRepository transactionRepository;
  private final RiskScoringService riskScoringService;

  public TransactionService(
      TransactionRepository transactionRepository, RiskScoringService riskScoringService) {
    this.transactionRepository = transactionRepository;
    this.riskScoringService = riskScoringService;
  }

  public RiskAssessment submit(TransactionRequest request) {
    Transaction transaction =
        new Transaction(
            null,
            request.getCustomerId(),
            request.getAmount(),
            request.getCurrency(),
            request.getCountry(),
            request.getMerchantCategory(),
            request.getTimestamp());
    Transaction saved = transactionRepository.save(transaction);
    return riskScoringService.assess(saved);
  }
}
