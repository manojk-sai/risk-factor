package com.manoj.risk.repository;

import com.manoj.risk.model.RiskAssessment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RiskAssessmentRepository extends MongoRepository<RiskAssessment, String> {
    RiskAssessment findByTransactionId(String transactionId);
    List<RiskAssessment> findAllByOrderByAssessedAtDesc();
    List<RiskAssessment> findByTransactionIdOrderByAssessedAtDesc(String transactionId);
}
