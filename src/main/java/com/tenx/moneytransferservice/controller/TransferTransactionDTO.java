package com.tenx.moneytransferservice.controller;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransferTransactionDTO {

    private Long sourceAccountId;
    private Long targetAccountId;
    private BigDecimal amount;
}
