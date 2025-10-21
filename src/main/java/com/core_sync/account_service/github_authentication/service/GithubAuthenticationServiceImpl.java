package com.core_sync.account_service.github_authentication.service;
import com.core_sync.account_service.account.entity.Account;
import com.core_sync.account_service.account_profile.entity.AccountProfile;
import com.core_sync.account_service.account_profile.repository.AccountProfileRepository;
import com.core_sync.account_service.github_authentication.entity.GithubAccessToken;
import com.core_sync.account_service.github_authentication.entity.GithubEmail;
import com.core_sync.account_service.github_authentication.entity.GithubNickname;
import com.core_sync.account_service.github_authentication.service.response.GithubLoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class GithubAuthenticationServiceImpl implements GithubAuthenticationService {

    private final String loginUrl;
    private final String clientId;
    private final String redirectUri;

    private final String clientSecret;
    private final String tokenRequestUri;

    private final String userInfoRequestUri;

    private final RestTemplate restTemplate;
    private final AccountProfileRepository accountProfileRepository;

    public GithubAuthenticationServiceImpl(
            @Value("${github.login-url}") String loginUrl,
            @Value("${github.client-id}") String clientId,
            @Value("${github.redirect-uri}") String redirectUri,
            @Value("${github.client-secret}") String clientSecret,
            @Value("${github.token-request-uri}") String tokenRequestUri,
            @Value("${github.user-info-request-uri}") String userInfoRequestUri,
            RestTemplate restTemplate, AccountProfileRepository accountProfileRepository) {

        this.loginUrl = loginUrl;
        this.clientId = clientId;
        this.redirectUri = redirectUri;

        this.clientSecret = clientSecret;
        this.tokenRequestUri = tokenRequestUri;

        this.userInfoRequestUri = userInfoRequestUri;

        this.restTemplate = restTemplate;
        this.accountProfileRepository = accountProfileRepository;
    }

    @Override
    public String requestOauthLink() {
        String state = UUID.randomUUID().toString();
        String formattedOauthLink = String.format("%s?client_id=%s&redirect_uri=%s&scope=read:user user:email&state=%s",
                loginUrl, clientId, redirectUri, state);

        return formattedOauthLink;
    }

    @Override
    public GithubLoginResponse handleLogin(String code) {
        GithubAccessToken accessToken = new GithubAccessToken(
                (String) getAccessToken(code).get("access_token")
        );

        Map<String, Object> userInfo = getUserInfo(accessToken.getValue());

        GithubEmail email = new GithubEmail(
                userInfo.get("email") != null ? (String) userInfo.get("email") : getPrimaryEmail(accessToken.getValue())
        );

        GithubNickname nickname = new GithubNickname(
                userInfo.get("name") != null ? (String) userInfo.get("name") : (String) userInfo.get("login")
        );

        log.info("email: {}, nickname: {}", email.getValue(), nickname.getValue());

        Optional<AccountProfile> maybeProfile = accountProfileRepository.findWithAccountByEmail(email.getValue());
        Account account = null;

        boolean isNewUser = maybeProfile.isEmpty();
        Long accountId = isNewUser
                ? 0
                : maybeProfile.get().getAccount().getId();

        return GithubLoginResponse.from(isNewUser, accessToken.getValue(), nickname.getValue(), email.getValue(), accountId);
    }

    private Map<String, Object> getAccessToken(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("redirect_uri", redirectUri);
        formData.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    tokenRequestUri,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new IllegalStateException("GitHub OAuth 토큰 요청 실패: " + response.getStatusCode());
            }

            return response.getBody();

        } catch (Exception e) {
            log.error("GitHub OAuth 토큰 발급 실패: {}", e.getMessage(), e);
            throw new RuntimeException("GitHub 토큰 발급 중 오류가 발생했습니다.", e);
        }
    }

    private Map<String, Object> getUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    userInfoRequestUri,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new IllegalStateException("GitHub 사용자 정보 요청 실패: " + response.getStatusCode());
            }

            Map<String, Object> body = response.getBody();

            if (!body.containsKey("id") || !body.containsKey("login")) {
                throw new IllegalStateException("GitHub 사용자 정보에 필요한 필드가 없습니다: " + body);
            }

            return body;

        } catch (Exception e) {
            log.error("GitHub 사용자 정보 요청 실패: {}", e.getMessage(), e);
            throw new RuntimeException("GitHub 사용자 정보 조회 중 오류가 발생했습니다.", e);
        }
    }

    private String getPrimaryEmail(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    "https://api.github.com/user/emails",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                throw new IllegalStateException("GitHub 이메일 조회 실패: " + response.getStatusCode());
            }

            List<Map<String, Object>> emailList = response.getBody();

            return emailList.stream()
                    .filter(e -> Boolean.TRUE.equals(e.get("primary")))
                    .map(e -> (String) e.get("email"))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("기본 이메일을 찾을 수 없습니다."));

        } catch (Exception e) {
            log.error("GitHub 기본 이메일 조회 실패: {}", e.getMessage(), e);
            throw new RuntimeException("GitHub 기본 이메일 조회 중 오류가 발생했습니다.", e);
        }
    }
}
