package com.core_sync.account_service.account.controller.response_form;

import lombok.Getter;

@Getter
public class AccountResponseForm {

    private Long accountId;
    private String nickname;

    public AccountResponseForm() {
    }

    public AccountResponseForm(Long accountId) {
        this.accountId = accountId;
    }
    
    public AccountResponseForm(Long accountId, String nickname) {
        this.accountId = accountId;
        this.nickname = nickname;
    }
}
