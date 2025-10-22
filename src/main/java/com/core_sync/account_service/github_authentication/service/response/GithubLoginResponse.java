package com.core_sync.account_service.github_authentication.service.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GithubLoginResponse {
    private final boolean newUser;
    private final String token;
    private final String nickname;
    private final String email;
    private final Long accountId;

    public static GithubLoginResponse from(boolean newUser, String token, String nickname, String email, Long accountId) {
        return new GithubLoginResponse(newUser, token, nickname, email, accountId);
    }
}
