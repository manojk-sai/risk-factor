package com.manoj.risk.repository;

import com.manoj.risk.model.RiskAssessment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RiskAssessmentRepository extends MongoRepository<RiskAssessment, String> {}
