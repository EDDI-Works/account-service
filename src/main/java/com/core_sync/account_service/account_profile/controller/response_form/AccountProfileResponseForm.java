package com.core_sync.account_service.account_profile.controller.response_form;

import lombok.Getter;

@Getter
public class AccountProfileResponseForm {

    private Long accountProfileId;
    private Long accountId;
    private String nickname;
    private String email;

    public AccountProfileResponseForm() {
    }

    public AccountProfileResponseForm(Long accountProfileId, Long accountId, String nickname, String email) {
        this.accountProfileId = accountProfileId;
        this.accountId = accountId;
        this.nickname = nickname;
        this.email = email;
    }
}
