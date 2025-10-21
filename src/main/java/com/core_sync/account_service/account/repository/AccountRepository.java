package com.core_sync.account_service.account.repository;

import com.core_sync.account_service.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    // email은 AccountProfile에서 관리
}
