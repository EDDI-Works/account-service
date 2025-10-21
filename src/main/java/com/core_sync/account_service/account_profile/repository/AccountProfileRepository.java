package com.core_sync.account_service.account_profile.repository;


import com.core_sync.account_service.account.entity.Account;
import com.core_sync.account_service.account_profile.entity.AccountProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountProfileRepository extends JpaRepository<AccountProfile, Long> {
    Optional<AccountProfile> findByAccount(Account account);

    @Query("SELECT ap FROM AccountProfile ap JOIN FETCH ap.account WHERE ap.email = :email")
    Optional<AccountProfile> findWithAccountByEmail(@Param("email") String email);

    AccountProfile findByAccountId(Long accountId);

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}
