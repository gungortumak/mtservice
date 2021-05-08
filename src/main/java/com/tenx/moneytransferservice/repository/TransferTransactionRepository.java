package com.tenx.moneytransferservice.repository;

import com.tenx.moneytransferservice.model.TransferTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransferTransactionRepository extends JpaRepository<TransferTransaction,Long> {
}
