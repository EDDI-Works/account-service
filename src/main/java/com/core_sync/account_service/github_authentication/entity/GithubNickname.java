package com.core_sync.account_service.github_authentication.entity;

import lombok.Getter;

@Getter
public class GithubNickname {
    private final String value;

    public GithubNickname(String value) {
        if (value == null || value.isBlank()) {
            this.value = "github_user"; // 기본 닉네임
        } else {
            this.value = value;
        }
    }
}
