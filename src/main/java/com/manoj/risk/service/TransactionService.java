package com.manoj.risk.service;

import com.manoj.risk.dto.RiskAssessmentResponse;
import com.manoj.risk.dto.TransactionRequest;
import com.manoj.risk.dto.TransactionSummaryResponse;
import com.manoj.risk.mapper.TransactionMapper;
import com.manoj.risk.model.CustomerRiskProfile;
import com.manoj.risk.model.RiskAssessment;
import com.manoj.risk.model.RiskLevel;
import com.manoj.risk.model.Transaction;
import com.manoj.risk.repository.CustomerRiskProfileRepository;
import com.manoj.risk.repository.RiskAssessmentRepository;
import com.manoj.risk.repository.TransactionRepository;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@Service
public class TransactionService {
  private static final Logger logger = org.slf4j.LoggerFactory.getLogger(TransactionService.class);
  private final TransactionRepository transactionRepository;
  private final RiskAssessmentRepository riskAssessmentRepository;
  private final RiskScoringService riskScoringService;
  private final CustomerRiskProfileRepository customerRiskProfileRepository;
  private final TransactionMapper transactionMapper;

  public TransactionService(
      TransactionRepository transactionRepository,
      RiskAssessmentRepository riskAssessmentRepository,
      RiskScoringService riskScoringService,
      CustomerRiskProfileRepository customerRiskProfileRepository,
      TransactionMapper transactionMapper) {
    this.transactionRepository = transactionRepository;
    this.riskScoringService = riskScoringService;
    this.riskAssessmentRepository = riskAssessmentRepository;
    this.customerRiskProfileRepository = customerRiskProfileRepository;
    this.transactionMapper =transactionMapper;
  }

  public RiskAssessment submit(TransactionRequest request) {
    logger.info("Submitting transaction for customer: {} amount: {} currency: {} country: {} merchantCategory: {}", request.getCustomerId(), request.getAmount(), request.getCurrency(), request.getCountry(), request.getMerchantCategory());
    Transaction transaction = transactionMapper.toTransaction(request);
    Transaction saved = transactionRepository.save(transaction);
    logger.info("Transaction submitted with ID: {}", saved.getId());
    RiskAssessment assessment = riskScoringService.assess(saved);
    logger.info("Risk assessment completed for transactionId: {} score: {} level: {}", assessment.getTransactionId(), assessment.getScore(), assessment.getLevel());
    updateCustomerRiskProfile(saved,assessment);
    return assessment;
  }

  public Page<TransactionSummaryResponse> listTransactions(
          String customerId,
          RiskLevel riskLevel,
          Instant fromDate,
          Instant toDate,
          Pageable pageable) {
    logger.info("Listing transactions with filters - customerId: {}, riskLevel: {}, fromDate: {}, toDate: {}", customerId, riskLevel, fromDate, toDate);
    Page<Transaction> transactionsPage = fetchTransactions(customerId, fromDate, toDate, pageable);
    List<TransactionSummaryResponse> summaries = transactionsPage.getContent().stream().map(
            transaction -> TransactionSummaryResponse.from(
                    transaction,
                    riskAssessmentRepository.findByTransactionId(transaction.getId())))
            .filter(
                    summary -> riskLevel == null || (summary.getRiskLevel()!=null && summary.getRiskLevel() == riskLevel)
            ).toList();
           long total = riskLevel == null  ? transactionsPage.getTotalElements() : summaries.size();
      return new PageImpl<>(summaries, pageable, total);
  }

  public List<RiskAssessmentResponse> riskAssessmentResponseList(String transactionId) {
    List<RiskAssessment> assessments = riskAssessmentRepository.findByTransactionIdOrderByAssessedAtDesc(transactionId);
    logger.info("Found {} risk assessments for transactionId: {}", assessments.size(), transactionId);
    return assessments.stream().map(RiskAssessmentResponse::from).toList();
  }
  private Page<Transaction> fetchTransactions(
          String customerId,
          Instant fromDate,
          Instant toDate,
          Pageable pageable) {
    if (customerId != null && fromDate != null && toDate != null) {
      return transactionRepository.findByCustomerIdAndTimestampBetween(
              customerId, fromDate, toDate, pageable);
    } else if (customerId != null && fromDate != null) {
      return transactionRepository.findByCustomerIdAndTimestampAfter(
              customerId, fromDate, pageable);
    } else if (customerId != null && toDate != null) {
      return transactionRepository.findByCustomerIdAndTimestampBefore(
              customerId, toDate, pageable);
    } else if (fromDate != null && toDate != null) {
      return transactionRepository.findByTimestampBetween(fromDate, toDate, pageable);
    } else if (customerId != null) {
      return transactionRepository.findByCustomerId(customerId, pageable);
    } else if (fromDate != null) {
      return transactionRepository.findByTimestampAfter(fromDate, pageable);
    } else if (toDate != null) {
      return transactionRepository.findByTimestampBefore(toDate, pageable);
    } else {
      return transactionRepository.findAll(pageable);
    }
  }

  private void updateCustomerRiskProfile(Transaction transaction, RiskAssessment riskAssessment){
    CustomerRiskProfile existingProfile = customerRiskProfileRepository.findByCustomerId(transaction.getCustomerId());
    long previousCount = existingProfile != null ? existingProfile.getTransactionCount() : 0;
    long nextCount = previousCount + 1;
    BigDecimal previousAvgAmount = existingProfile != null ? existingProfile.getAvgAmount() : BigDecimal.ZERO;
    BigDecimal total = previousAvgAmount.multiply(BigDecimal.valueOf(previousCount)).add(transaction.getAmount());
    BigDecimal nextAvgAmount = total.divide(BigDecimal.valueOf(nextCount), 2, RoundingMode.HALF_UP);
    CustomerRiskProfile updatedProfile = new CustomerRiskProfile(
            transaction.getCustomerId(),
            riskAssessment.getLevel(),
            nextAvgAmount,
            nextCount,
            Instant.now()
    );
    customerRiskProfileRepository.save(updatedProfile);
  }
}
