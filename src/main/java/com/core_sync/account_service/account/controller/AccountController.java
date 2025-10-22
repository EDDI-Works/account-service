package com.core_sync.account_service.account.controller;

import com.core_sync.account_service.account.controller.request_form.RegisterNormalAccountRequestForm;
import com.core_sync.account_service.account.controller.response_form.AccountResponseForm;
import com.core_sync.account_service.account.entity.Account;
import com.core_sync.account_service.account.service.AccountService;
import com.core_sync.account_service.account.service.request.RegisterNormalAccountRequest;
import com.core_sync.account_service.account_profile.entity.AccountProfile;
import com.core_sync.account_service.account_profile.repository.AccountProfileRepository;
import com.core_sync.account_service.account_profile.service.AccountProfileService;
import com.core_sync.account_service.redis_cache.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountProfileService accountProfileService;
    private final AccountProfileRepository accountProfileRepository;
    private final RedisCacheService redisCacheService;

    @GetMapping("/health")

    @PostMapping("/register")
    public String register(@RequestHeader("Authorization") String authorizationHeader,
                           @RequestBody RegisterNormalAccountRequestForm requestForm) {
        log.info("회원 가입 요청: requestForm={}", requestForm);

        String temporaryUserToken = authorizationHeader.replace("Bearer ", "").trim();

        String accessToken = redisCacheService.getValueByKey(temporaryUserToken, String.class);
        if (accessToken == null) {
            throw new IllegalArgumentException("만료되었거나 잘못된 임시 토큰입니다.");
        }

        Account account = accountService.createAccount(requestForm.toRegisterNormalAccountRequest());
        accountProfileService.createAccountProfile(account, requestForm.toRegisterAccountProfileRequest());

        String userToken = issueUserToken(account.getId(), accessToken);
        redisCacheService.deleteByKey(temporaryUserToken);

        return userToken;
    }

    private String issueUserToken(Long accountId, String accessToken) {
        String userToken = UUID.randomUUID().toString();
        redisCacheService.setKeyAndValue(accountId, accessToken);
        redisCacheService.setKeyAndValue(userToken, accountId);
        return userToken;
    }

    @GetMapping("/{accountId}")
    public AccountResponseForm getAccount(@PathVariable("accountId") Long accountId) {
        return accountService.findById(accountId);
    }
    
    @GetMapping("/{accountId}/nickname")
    public java.util.Map<String, String> getNickname(@PathVariable("accountId") Long accountId) {
        log.info("닉네임 조회 요청: accountId={}", accountId);
        AccountProfile profile = accountProfileRepository.findByAccountId(accountId);
        String nickname = profile != null && profile.getNickname() != null && !profile.getNickname().trim().isEmpty() 
            ? profile.getNickname() 
            : "User " + accountId;
        
        java.util.Map<String, String> response = new java.util.HashMap<>();
        response.put("nickname", nickname);
        
        log.info("닉네임 조회 성공: accountId={}, nickname={}", accountId, nickname);
        return response;
    }
    
    @GetMapping("/find-by-email")
    public java.util.Map<String, Object> findByEmail(@RequestParam("email") String email) {
        log.info("이메일로 계정 조회: {}", email);
        
        // AccountProfile에서 이메일로 조회
        AccountProfile profile = accountProfileRepository.findWithAccountByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일로 등록된 계정을 찾을 수 없습니다: " + email));
        
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("accountId", profile.getAccount().getId());
        response.put("email", profile.getEmail());
        
        log.info("계정 조회 성공 - accountId: {}, email: {}", profile.getAccount().getId(), profile.getEmail());
        return response;
    }
    
    @PostMapping("/guest/register")
    public java.util.Map<String, Object> registerGuest(@RequestBody java.util.Map<String, String> request) throws Exception {
        log.info("게스트 회원가입 요청: {}", request);
        
        String nickname = request.get("nickname");
        String email = request.get("email");
        String loginCode = request.get("loginCode");
        
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("닉네임을 입력해주세요.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("이메일을 입력해주세요.");
        }
        if (loginCode == null || loginCode.trim().isEmpty()) {
            throw new IllegalArgumentException("로그인 코드를 입력해주세요.");
        }
        
        // 게스트 계정 생성 (AccountProfile도 함께 생성됨)
        Account account = accountService.createGuestAccount(email, loginCode, nickname);
        
        // 토큰 발급
        String userToken = UUID.randomUUID().toString();
        redisCacheService.setKeyAndValue(userToken, account.getId());
        
        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("userToken", userToken);
        response.put("accountId", account.getId());
        response.put("nickname", nickname);
        response.put("email", email);
        
        return response;
    }
    
    @PostMapping("/guest/login")
    public java.util.Map<String, Object> loginGuest(@RequestBody java.util.Map<String, String> request) {
        log.info("게스트 로그인 요청: {}", request);
        
        String email = request.get("email");
        String loginCode = request.get("loginCode");
        
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("이메일을 입력해주세요.");
        }
        if (loginCode == null || loginCode.trim().isEmpty()) {
            throw new IllegalArgumentException("로그인 코드를 입력해주세요.");
        }
        
        // 게스트 로그인
        Account account = accountService.loginGuest(email, loginCode);
        
        // 토큰 발급
        String userToken = UUID.randomUUID().toString();
        redisCacheService.setKeyAndValue(userToken, account.getId());
        
        // 프로필 정보 조회
        AccountProfile profile = accountProfileRepository.findByAccountId(account.getId());
        String nickname = profile != null ? profile.getNickname() : "";
        String profileEmail = profile != null ? profile.getEmail() : email;

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("userToken", userToken);
        response.put("accountId", account.getId());
        response.put("nickname", nickname);
        response.put("email", profileEmail);
        
        return response;
    }
}
