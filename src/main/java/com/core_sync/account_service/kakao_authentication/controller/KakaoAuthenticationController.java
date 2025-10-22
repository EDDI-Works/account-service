package com.core_sync.account_service.kakao_authentication.controller;

import com.core_sync.account_service.kakao_authentication.service.KakaoAuthenticationService;
import com.core_sync.account_service.kakao_authentication.service.response.KakaoLoginResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/authentication/kakao")
public class KakaoAuthenticationController {

    private final KakaoAuthenticationService kakaoAuthenticationService;

    @GetMapping("/link")
    public String linkKakaoAuthentication(){
        log.info("카카오 로그인 링크 진입");
        return kakaoAuthenticationService.requestKakaoAuthenticationLink();
    }

    @GetMapping("/login")
    public void kakaoLogin(@RequestParam("code") String code, HttpServletResponse response) throws Exception {
        log.info("Kakao Login Request");

        try {
            KakaoLoginResponse kakaoLoginResponse = kakaoAuthenticationService.handleLogin(code);
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write(kakaoLoginResponse.getHtmlResponse());
        } catch (Exception e) {
            log.error("Kakao 로그인 에러", e);

            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("카카오 로그인 실패: " + e.getMessage());
        }
    }
}
