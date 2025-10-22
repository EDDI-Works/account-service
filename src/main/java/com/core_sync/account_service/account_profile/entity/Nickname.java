package com.core_sync.account_service.account_profile.entity;

public class Nickname {
    private final String value;

    public Nickname(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("닉네임은 필수 항목입니다.");
        }
        if (value.length() > 30) {
            throw new IllegalArgumentException("닉네임은 30자 이하만 가능합니다.");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
