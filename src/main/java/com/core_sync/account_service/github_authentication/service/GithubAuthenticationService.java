package com.core_sync.account_service.github_authentication.service;


import com.core_sync.account_service.github_authentication.service.response.GithubLoginResponse;

public interface GithubAuthenticationService {
    String requestOauthLink();
    GithubLoginResponse handleLogin(String code);
}
