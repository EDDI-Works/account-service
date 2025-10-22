package com.core_sync.account_service.github_authentication.entity;

import lombok.Getter;

@Getter
public class GithubAccessToken {
    private final String value;

    public GithubAccessToken(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AccessToken은 비어있을 수 없습니다.");
        }
        this.value = value;
    }
}
