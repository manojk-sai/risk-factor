package com.manoj.risk.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.manoj.risk.dto.RiskAssessmentResponse;
import com.manoj.risk.dto.TransactionRequest;
import com.manoj.risk.mapper.TransactionMapper;
import com.manoj.risk.model.CustomerRiskProfile;
import com.manoj.risk.model.RiskAssessment;
import com.manoj.risk.model.RiskLevel;
import com.manoj.risk.model.Transaction;
import com.manoj.risk.repository.CustomerRiskProfileRepository;
import com.manoj.risk.repository.RiskAssessmentRepository;
import com.manoj.risk.repository.TransactionRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
  @Mock
  private TransactionRepository transactionRepository;
  @Mock
  private RiskAssessmentRepository riskAssessmentRepository;
  @Mock
  private RiskScoringService riskScoringService;
  @Mock
  private CustomerRiskProfileRepository customerRiskProfileRepository;
  @Mock
  private  TransactionMapper transactionMapper;
  @InjectMocks
  private TransactionService service;
  private final Pageable pageable = PageRequest.of(0, 10);
  @Test
  void submitStoresTransactionAndReturnsAssessment() {
    TransactionRequest request =
        new TransactionRequest(
            "cust-100",
            new BigDecimal("120"),
            "USD",
            "US",
            "ELECTRONICS",
            Instant.parse("2024-02-01T10:00:00Z"));

    Transaction realTransaction = new Transaction(
            null,
            "cust-100",
            new BigDecimal("120"),
            "USD",
            "US",
            "ELECTRONICS",
            Instant.parse("2024-02-01T10:00:00Z"));
    RiskAssessment assessment = new RiskAssessment(
            "ra-123",
            "tx-456",
            75,
            RiskLevel.MEDIUM,
            "Transaction amount within normal range",
            Instant.now()
    );
    CustomerRiskProfile existingProfile = new CustomerRiskProfile(
            "cust-100",
            RiskLevel.MEDIUM,
            BigDecimal.ZERO,
            0L,
            Instant.now()
    );
    when(transactionMapper.toTransaction(request)).thenReturn(realTransaction);
    when(transactionRepository.save(any(Transaction.class))).thenReturn(realTransaction);
    when(riskScoringService.assess(realTransaction)).thenReturn(assessment);
    when(customerRiskProfileRepository.findByCustomerId("cust-100")).thenReturn(existingProfile);

    RiskAssessment result = service.submit(request);
    assertThat(result).isEqualTo(assessment);

    ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
    verify(transactionRepository).save(transactionCaptor.capture());
    Transaction mappedTransaction = transactionCaptor.getValue();
    assertThat(mappedTransaction.getCustomerId()).isEqualTo("cust-100");
    assertThat(mappedTransaction.getAmount()).isEqualByComparingTo(new BigDecimal("120"));
    assertThat(mappedTransaction.getCurrency()).isEqualTo("USD");
    assertThat(mappedTransaction.getCountry()).isEqualTo("US");
    assertThat(mappedTransaction.getMerchantCategory()).isEqualTo("ELECTRONICS");
    assertThat(mappedTransaction.getTimestamp()).isEqualTo(Instant.parse("2024-02-01T10:00:00Z"));
    assertThat(mappedTransaction.getId()).isNull();

    verify(customerRiskProfileRepository).save(any(CustomerRiskProfile.class));
  }
@Test
  void submitCreatesNewCustomerRiskProfile(){
    TransactionRequest request =
        new TransactionRequest(
            "cust-200",
            new BigDecimal("150.00"),
            "USD",
            "US",
            "GROCERY",
            Instant.parse("2024-03-01T10:00:00Z"));

    Transaction savedTransaction = new Transaction(
            "tx-789",
            "cust-200",
            new BigDecimal("150.00"),
            "USD",
            "US",
            "GROCERY",
            Instant.parse("2024-03-01T10:00:00Z"));
  RiskAssessment assessment = new RiskAssessment(
          "ra-123",
          "tx-456",
          75,
          RiskLevel.MEDIUM,
          "Transaction amount within normal range",
          Instant.now()
  );

    when(transactionMapper.toTransaction(request)).thenReturn(savedTransaction);
    when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
    when(riskScoringService.assess(savedTransaction)).thenReturn(assessment);
    when(customerRiskProfileRepository.findByCustomerId("cust-200")).thenReturn(null);
     service.submit(request);

  }
  @Test
  void submitUpdatesExistingCustomerRiskProfile() {
    TransactionRequest request =
            new TransactionRequest("cust-300", new BigDecimal("40.00"),"USD","US","FUEL", Instant.parse("2024-02-02T10:00:00Z"));
    Transaction savedTransaction = new Transaction(
            "tx-300",
            "cust-300",
            new BigDecimal("40.00"),
            "USD",
            "US",
            "FUEL",
            Instant.parse("2024-02-02T10:00:00Z"));
    RiskAssessment assessment = new RiskAssessment(
            "ra-300",
            "tx-300",
            20,
            RiskLevel.LOW,
            "Low risk transaction",
            Instant.parse("2024-02-02T10:05:00Z"));
    CustomerRiskProfile existingProfile =
            new CustomerRiskProfile(
                    "cust-300",
                    RiskLevel.LOW,
                    new BigDecimal("30.00"),
                    (long) 2,
                    Instant.parse("2024-01-15T10:00:00Z"));

    when(transactionMapper.toTransaction(request)).thenReturn(savedTransaction);
    when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);
    when(riskScoringService.assess(savedTransaction)).thenReturn(assessment);
    when(customerRiskProfileRepository.findByCustomerId("cust-300")).thenReturn(existingProfile);

    service.submit(request);

    verify(transactionRepository).save(savedTransaction);
    verify(riskScoringService).assess(savedTransaction);
    verify(customerRiskProfileRepository).save(any(CustomerRiskProfile.class));
  }
  @Test
  void listAssessmentsReturnsSortedAssessments() {
    RiskAssessment newest =
            new RiskAssessment(
                    "ra-2",
                    "tx-900",
                    90,
                    RiskLevel.HIGH,
                    "Newest assessment",
                    Instant.parse("2024-06-01T10:10:00Z"));
    RiskAssessment older =
            new RiskAssessment(
                    "ra-1",
                    "tx-900",
                    40,
                    RiskLevel.MEDIUM,
                    "Older assessment",
                    Instant.parse("2024-06-01T09:10:00Z"));

    when(riskAssessmentRepository.findByTransactionIdOrderByAssessedAtDesc("tx-900"))
            .thenReturn(List.of(newest, older));

    List<RiskAssessmentResponse> responses = service.riskAssessmentResponseList("tx-900");

    assertThat(responses).hasSize(2);
    assertThat(responses.get(0).getId()).isEqualTo("ra-2");
    assertThat(responses.get(1).getId()).isEqualTo("ra-1");
  }

  @Test
  void listAssessmentsReturnsEmptyListWhenNoAssessments() {
    when(riskAssessmentRepository.findByTransactionIdOrderByAssessedAtDesc("tx-901"))
            .thenReturn(List.of());
    List<RiskAssessmentResponse> responses = service.riskAssessmentResponseList("tx-901");
    assertThat(responses).isEmpty();
  }

  @Test
  void listTransactionsFiltersByRiskLevel() {
    Transaction highTransaction =
        new Transaction(
            "tx-high",
            "cust-400",
            new BigDecimal("600"),
            "USD",
            "US",
            "ELECTRONICS",
            Instant.parse("2024-04-01T10:00:00Z"));
    Transaction lowTransaction =
        new Transaction(
            "tx-low",
            "cust-400",
            new BigDecimal("40"),
            "USD",
            "US",
            "GROCERY",
            Instant.parse("2024-04-02T10:00:00Z"));
    when(transactionRepository.findAll(pageable))
        .thenReturn(pageOf(highTransaction, lowTransaction));
    when(riskAssessmentRepository.findByTransactionId("tx-high"))
        .thenReturn(
            new RiskAssessment(
                "ra-high",
                "tx-high",
                80,
                RiskLevel.HIGH,
                "High risk",
                Instant.parse("2024-04-01T10:05:00Z")));
    when(riskAssessmentRepository.findByTransactionId("tx-low"))
        .thenReturn(
            new RiskAssessment(
                "ra-low",
                "tx-low",
                15,
                RiskLevel.LOW,
                "Low risk",
                Instant.parse("2024-04-02T10:05:00Z")));

    var result = service.listTransactions(null, RiskLevel.HIGH, null, null, pageable);

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getTransactionId()).isEqualTo("tx-high");
    assertThat(result.getTotalElements()).isEqualTo(1);
  }

  @Test
  void listTransactionsFetchesByCustomerAndDateRange() {
    Instant fromDate = Instant.parse("2024-01-01T00:00:00Z");
    Instant toDate = Instant.parse("2024-01-31T23:59:59Z");
    Transaction transaction = sampleTransaction("tx-1");
    when(transactionRepository.findByCustomerIdAndTimestampBetween(
            "cust-1", fromDate, toDate, pageable))
        .thenReturn(pageOf(transaction));
    when(riskAssessmentRepository.findByTransactionId("tx-1")).thenReturn(sampleAssessment("tx-1"));

    Page<TransactionSummaryResponse> result =
        service.listTransactions("cust-1", null, fromDate, toDate, pageable);

    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void listTransactionsFetchesByCustomerAndFromDate() {
    Instant fromDate = Instant.parse("2024-02-01T00:00:00Z");
    Transaction transaction = sampleTransaction("tx-2");
    when(transactionRepository.findByCustomerIdAndTimestampAfter("cust-2", fromDate, pageable))
        .thenReturn(pageOf(transaction));
    when(riskAssessmentRepository.findByTransactionId("tx-2")).thenReturn(sampleAssessment("tx-2"));

    Page<TransactionSummaryResponse> result =
        service.listTransactions("cust-2", null, fromDate, null, pageable);

    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void listTransactionsFetchesByCustomerAndToDate() {
    Instant toDate = Instant.parse("2024-02-15T23:59:59Z");
    Transaction transaction = sampleTransaction("tx-3");
    when(transactionRepository.findByCustomerIdAndTimestampBefore("cust-3", toDate, pageable))
        .thenReturn(pageOf(transaction));
    when(riskAssessmentRepository.findByTransactionId("tx-3")).thenReturn(sampleAssessment("tx-3"));

    Page<TransactionSummaryResponse> result =
        service.listTransactions("cust-3", null, null, toDate, pageable);

    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void listTransactionsFetchesByDateRangeOnly() {
    Instant fromDate = Instant.parse("2024-03-01T00:00:00Z");
    Instant toDate = Instant.parse("2024-03-31T23:59:59Z");
    Transaction transaction = sampleTransaction("tx-4");
    when(transactionRepository.findByTimestampBetween(fromDate, toDate, pageable))
        .thenReturn(pageOf(transaction));
    when(riskAssessmentRepository.findByTransactionId("tx-4")).thenReturn(sampleAssessment("tx-4"));

    Page<TransactionSummaryResponse> result =
        service.listTransactions(null, null, fromDate, toDate, pageable);

    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void listTransactionsFetchesByCustomerOnly() {
    Transaction transaction = sampleTransaction("tx-5");
    when(transactionRepository.findByCustomerId("cust-5", pageable))
        .thenReturn(pageOf(transaction));
    when(riskAssessmentRepository.findByTransactionId("tx-5")).thenReturn(sampleAssessment("tx-5"));

    Page<TransactionSummaryResponse> result =
        service.listTransactions("cust-5", null, null, null, pageable);

    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void listTransactionsFetchesByFromDateOnly() {
    Instant fromDate = Instant.parse("2024-04-01T00:00:00Z");
    Transaction transaction = sampleTransaction("tx-6");
    when(transactionRepository.findByTimestampAfter(fromDate, pageable))
        .thenReturn(pageOf(transaction));
    when(riskAssessmentRepository.findByTransactionId("tx-6")).thenReturn(sampleAssessment("tx-6"));

    Page<TransactionSummaryResponse> result =
        service.listTransactions(null, null, fromDate, null, pageable);

    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void listTransactionsFetchesByToDateOnly() {
    Instant toDate = Instant.parse("2024-05-01T23:59:59Z");
    Transaction transaction = sampleTransaction("tx-7");
    when(transactionRepository.findByTimestampBefore(toDate, pageable))
        .thenReturn(pageOf(transaction));
    when(riskAssessmentRepository.findByTransactionId("tx-7")).thenReturn(sampleAssessment("tx-7"));

    Page<TransactionSummaryResponse> result =
        service.listTransactions(null, null, null, toDate, pageable);

    assertThat(result.getContent()).hasSize(1);
  }

  @Test
  void listTransactionsFetchesAllWhenNoFilters() {
    Transaction transaction = sampleTransaction("tx-8");
    when(transactionRepository.findAll(pageable)).thenReturn(pageOf(transaction));
    when(riskAssessmentRepository.findByTransactionId("tx-8")).thenReturn(sampleAssessment("tx-8"));

    Page<TransactionSummaryResponse> result =
        service.listTransactions(null, null, null, null, pageable);

    assertThat(result.getContent()).hasSize(1);
  }

  private Page<Transaction> pageOf(Transaction... transactions) {
    return new PageImpl<>(List.of(transactions), pageable, transactions.length);
  }

  private Transaction sampleTransaction(String id) {
    return new Transaction(
        id,
        "cust-" + id,
        new BigDecimal("120.00"),
        "USD",
        "US",
        "ELECTRONICS",
        Instant.parse("2024-02-01T10:00:00Z"));
  }

  private RiskAssessment sampleAssessment(String transactionId) {
    return new RiskAssessment(
        "ra-" + transactionId,
        transactionId,
        50,
        RiskLevel.MEDIUM,
        "Moderate risk",
        Instant.parse("2024-02-01T10:05:00Z"));
  }
}
