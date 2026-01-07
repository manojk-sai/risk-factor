package com.manoj.risk.mapper;

import com.manoj.risk.dto.TransactionRequest;
import com.manoj.risk.model.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {
    public Transaction toTransaction(TransactionRequest request){
        return new Transaction(
                null,
                request.getCustomerId(),
                request.getAmount(),
                request.getCurrency(),
                request.getCountry(),
                request.getMerchantCategory(),
                request.getTimestamp());
    }
}
