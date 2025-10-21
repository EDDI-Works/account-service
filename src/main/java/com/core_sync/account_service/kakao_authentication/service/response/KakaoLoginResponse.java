package com.core_sync.account_service.kakao_authentication.service.response;

import lombok.Getter;

@Getter
public abstract class KakaoLoginResponse {



    public static KakaoLoginResponse of(boolean isNewUser, String token, String nickname, String email, String origin) {
        return isNewUser
                ? new NewUserKakaoLoginResponse(isNewUser, token, nickname, email, origin)
                : new ExistingUserKakaoLoginResponse(isNewUser, token, nickname, email, origin);
    }





    public abstract String getHtmlResponse();
    public abstract String getUserToken();
    protected static String escape(String str) {
        return str.replace("'", "\\'");
    }

}

