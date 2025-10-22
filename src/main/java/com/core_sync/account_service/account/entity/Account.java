package com.core_sync.account_service.account.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_type_id", nullable = false)
    private AccountRoleType accountRoleType;
    
    @Column(name = "login_code")
    private String loginCode;

    public Account(Long id) {
        this.id = id;
    }

    public Account() {

    }

    public Account(AccountRoleType accountRoleType) {
        this.accountRoleType = accountRoleType;
    }
    
    public Account(AccountRoleType accountRoleType, String loginCode) {
        this.accountRoleType = accountRoleType;
        this.loginCode = loginCode;
    }
}
