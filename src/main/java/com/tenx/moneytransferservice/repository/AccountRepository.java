package com.tenx.moneytransferservice.repository;

import com.tenx.moneytransferservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import javax.persistence.LockModeType;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    @Override
    @Lock(LockModeType.OPTIMISTIC)
    Optional<Account> findById(Long aLong);
}
