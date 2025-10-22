package com.core_sync.account_service.account.service.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountEmailResponse {
    private Long accountId;
    private String email;
}
