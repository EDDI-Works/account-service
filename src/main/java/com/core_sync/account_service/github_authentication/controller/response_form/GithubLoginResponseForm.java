package com.core_sync.account_service.github_authentication.controller.response_form;

import com.core_sync.account_service.account.entity.LoginType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GithubLoginResponseForm {
    private final boolean newUser;
    private final String token;
    private final String nickname;
    private final String email;
    private final LoginType loginType;

    public static GithubLoginResponseForm from(boolean newUser, String token, String nickname, String email, LoginType loginType) {
        return new GithubLoginResponseForm(newUser, token, nickname, email, loginType);
    }
}