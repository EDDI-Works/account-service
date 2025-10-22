package com.core_sync.account_service.account_profile.entity;

public class Email {
    private final String value;

    public Email(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("이메일은 필수 항목입니다.");
        }
        if (!value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

