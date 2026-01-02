package com.manoj.risk.controller;

import com.manoj.risk.dto.TransactionRequest;
import com.manoj.risk.dto.TransactionResponse;
import com.manoj.risk.model.RiskAssessment;
import com.manoj.risk.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
  private final TransactionService transactionService;

  public TransactionController(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public TransactionResponse submit(@Valid @RequestBody TransactionRequest request) {
    RiskAssessment assessment = transactionService.submit(request);
    return new TransactionResponse(
        assessment.getTransactionId(),
        assessment.getScore(),
        assessment.getLevel(),
        assessment.getReason(),
        assessment.getAssessedAt());
  }
}
