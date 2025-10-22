package com.core_sync.account_service.account.controller.request_form;

import com.core_sync.account_service.account.entity.LoginType;
import com.core_sync.account_service.account.service.request.RegisterNormalAccountRequest;
import com.core_sync.account_service.account_profile.service.request.RegisterAccountProfileRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RegisterNormalAccountRequestForm {
    final private String email;
    final private String nickname;
    final private LoginType loginType;

    public RegisterNormalAccountRequest toRegisterNormalAccountRequest() {
        return new RegisterNormalAccountRequest(loginType);
    }

    public RegisterAccountProfileRequest toRegisterAccountProfileRequest() {
        return new RegisterAccountProfileRequest(email, nickname);
    }
}
