package com.manoj.risk.controller;

import com.manoj.risk.dto.RiskAssessmentResponse;
import com.manoj.risk.dto.TransactionRequest;
import com.manoj.risk.dto.TransactionResponse;
import com.manoj.risk.dto.TransactionSummaryResponse;
import com.manoj.risk.model.RiskAssessment;
import com.manoj.risk.model.RiskLevel;
import com.manoj.risk.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@Tag(name = "Transactions", description = "Operations for risk-aware transaction monitoring.")
public class TransactionController {
  private final TransactionService transactionService;

  public TransactionController(TransactionService transactionService) {
    this.transactionService = transactionService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @Operation(
      summary = "Submit a transaction for risk assessment",
      description = "Evaluates a transaction payload and returns the assessed risk details.")
  public TransactionResponse submit(
      @Valid
          @io.swagger.v3.oas.annotations.parameters.RequestBody(
              description = "Transaction payload to evaluate",
              required = true)
          @RequestBody
          TransactionRequest request) {
    RiskAssessment assessment = transactionService.submit(request);
    return new TransactionResponse(
        assessment.getTransactionId(),
        assessment.getScore(),
        assessment.getLevel(),
        assessment.getReason(),
        assessment.getAssessedAt());
  }

  @GetMapping
  @Operation(
      summary = "List recent transactions",
      description = "Returns a paginated list of transactions with optional filters.")
  public Page<TransactionSummaryResponse> list(
      @Parameter(description = "Filter by customer identifier", example = "cust-001")
          @RequestParam(required = false)
          String customerId,
      @Parameter(description = "Filter by risk level", example = "HIGH")
          @RequestParam(required = false)
          RiskLevel riskLevel,
      @Parameter(
              description = "Include transactions from this timestamp (ISO-8601)",
              example = "2024-08-21T12:30:00Z")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          Instant fromDate,
      @Parameter(
              description = "Include transactions up to this timestamp (ISO-8601)",
              example = "2024-08-22T12:30:00Z")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          Instant toDate,
      @Parameter(description = "Page index (0-based)", example = "0")
          @RequestParam(defaultValue = "0")
          int page,
      @Parameter(description = "Page size", example = "5")
          @RequestParam(defaultValue = "5")
          int size) {
    PageRequest pageRequest = PageRequest.of(page, size, Sort.by("timestamp").descending());
    return transactionService.listTransactions(customerId, riskLevel, fromDate, toDate, pageRequest);
  }

    @GetMapping("/{id}/assessments")
    @Operation(
            summary = "List assessments for a transaction",
            description = "Returns risk assessments for a transaction ordered by assessment time.")
    public List<RiskAssessmentResponse> listAssessments(
            @Parameter(description = "Transaction identifier", example = "txn-12345")
            @PathVariable("id")
            String transactionId) {
      System.out.println("Fetching risk assessments for transactionId: " + transactionId);
        return transactionService.riskAssessmentResponseList(transactionId);
    }
}
