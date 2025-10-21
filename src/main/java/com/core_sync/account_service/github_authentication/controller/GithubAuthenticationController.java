package com.core_sync.account_service.github_authentication.controller;

import com.core_sync.account_service.account.entity.LoginType;
import com.core_sync.account_service.github_authentication.controller.response_form.GithubLoginResponseForm;
import com.core_sync.account_service.github_authentication.service.GithubAuthenticationService;
import com.core_sync.account_service.github_authentication.service.response.GithubLoginResponse;
import com.core_sync.account_service.redis_cache.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/github-authentication")
@RequiredArgsConstructor
public class GithubAuthenticationController {

    private final GithubAuthenticationService githubAuthenticationService;
    private final RedisCacheService redisCacheService;

    @GetMapping("/request-oauth-link")
    public String githubOauthLink() {
        return githubAuthenticationService.requestOauthLink();
    }

    @GetMapping("/login")
    public GithubLoginResponseForm githubLogin(@RequestParam("code") String code) throws Exception {
        log.info("Github Login Request");

        try {
            GithubLoginResponse loginResponse = githubAuthenticationService.handleLogin(code);
            String redisToken = loginResponse.isNewUser()
                    ? createTemporaryUserToken(loginResponse.getToken())
                    : createUserTokenWithAccessToken(loginResponse.getAccountId(), loginResponse.getToken());

            return GithubLoginResponseForm.from(
                    loginResponse.isNewUser(),
                    redisToken,
                    loginResponse.getNickname(),
                    loginResponse.getEmail(),
                    LoginType.GITHUB
            );
        } catch (Exception e) {
            log.error("Github 로그인 에러", e);
            return null;
        }
    }

    private String createUserTokenWithAccessToken(Long accountId, String accessToken) {
        try {
            String userToken = UUID.randomUUID().toString();
            
            // GitHub Access Token을 github:token:{accountId} 키로 저장
            String githubTokenKey = "github:token:" + accountId;
            redisCacheService.setKeyAndValue(githubTokenKey, accessToken);
            
            // 사용자 토큰 매핑 저장
            redisCacheService.setKeyAndValue(userToken, accountId);
            
            log.info("GitHub 로그인 토큰 저장 - key: {}", githubTokenKey);
            
            return userToken;
        } catch (Exception e) {
            throw new RuntimeException("Error storing token in Redis: " + e.getMessage());
        }
    }

    private String createTemporaryUserToken(String accessToken) {
        try {
            String userToken = UUID.randomUUID().toString();
            redisCacheService.setKeyAndValue(userToken, accessToken, Duration.ofMinutes(5));
            return userToken;
        } catch (Exception e) {
            throw new RuntimeException("Error storing token in Redis: " + e.getMessage());
        }
    }

    /**
     * 기존 사용자가 GitHub 저장소 연동을 위해 GitHub OAuth를 진행하는 엔드포인트
     * Redis에 "github:token:{accountId}" 형식으로 GitHub Access Token 저장
     */
    @GetMapping("/link")
    public String linkGithubAccount(@RequestParam("code") String code, @RequestParam("state") String accountId) {
        log.info("GitHub 계정 연동 요청 - accountId: {}", accountId);

        try {
            GithubLoginResponse loginResponse = githubAuthenticationService.handleLogin(code);
            
            // GitHub Access Token을 별도 키로 저장
            String githubTokenKey = "github:token:" + accountId;
            redisCacheService.setKeyAndValue(githubTokenKey, loginResponse.getToken());
            
            log.info("GitHub 토큰 저장 완료 - key: {}, token 길이: {}", githubTokenKey, loginResponse.getToken().length());
            
            // 프론트엔드로 리다이렉트 (성공 메시지와 함께)
            return "<html><body><script>window.opener.postMessage({type: 'github-link-success'}, '*'); window.close();</script></body></html>";
        } catch (Exception e) {
            log.error("GitHub 계정 연동 실패", e);
            return "<html><body><script>alert('GitHub 계정 연동 실패: " + e.getMessage() + "'); window.close();</script></body></html>";
        }
    }
}
