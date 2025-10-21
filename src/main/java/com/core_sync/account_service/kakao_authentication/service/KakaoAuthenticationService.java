package com.core_sync.account_service.kakao_authentication.service;

import com.core_sync.account_service.kakao_authentication.service.response.KakaoLoginResponse;

import java.util.Map;

public interface KakaoAuthenticationService {
    String requestKakaoAuthenticationLink();
    Map<String, Object> getAccessToken(String code);
    Map<String, Object> getUserInfo(String accessToken);
    KakaoLoginResponse handleLogin(String code);
    String extractNickname(Map<String, Object> userInfo);
    String extractEmail(Map<String, Object> userInfo);

}
