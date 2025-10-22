package com.core_sync.account_service.account.service.request;

import com.core_sync.account_service.account.entity.LoginType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RegisterNormalAccountRequest {
    private final LoginType loginType;
}
