package com.core_sync.account_service.account.repository;

import com.core_sync.account_service.account.entity.AccountRoleType;
import com.core_sync.account_service.account.entity.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRoleTypeRepository extends JpaRepository<AccountRoleType, Long> {
    Optional<AccountRoleType> findByRoleType(RoleType roleType);
}
