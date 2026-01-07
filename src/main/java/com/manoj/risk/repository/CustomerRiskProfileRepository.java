package com.manoj.risk.repository;

import com.manoj.risk.model.CustomerRiskProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CustomerRiskProfileRepository extends MongoRepository<CustomerRiskProfile, String> {
    CustomerRiskProfile findByCustomerId(String customerId);
}
