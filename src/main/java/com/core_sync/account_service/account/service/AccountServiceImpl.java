package com.core_sync.account_service.account.service;


import com.core_sync.account_service.account.controller.response_form.AccountResponseForm;
import com.core_sync.account_service.account.entity.Account;
import com.core_sync.account_service.account.entity.AccountRoleType;
import com.core_sync.account_service.account.entity.RoleType;
import com.core_sync.account_service.account.repository.AccountRepository;
import com.core_sync.account_service.account.repository.AccountRoleTypeRepository;
import com.core_sync.account_service.account.service.request.RegisterNormalAccountRequest;
import com.core_sync.account_service.account.service.response.AccountEmailResponse;
import com.core_sync.account_service.account_profile.entity.AccountProfile;
import com.core_sync.account_service.account_profile.repository.AccountProfileRepository;
import com.core_sync.account_service.account_profile.service.AccountProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    final private AccountRepository accountRepository;
    final private AccountRoleTypeRepository accountRoleTypeRepository;
    final private AccountProfileService accountProfileService;
    final private AccountProfileRepository accountProfileRepository;

    @Override
    public Account createAccount(RegisterNormalAccountRequest request) {
        AccountRoleType accountRoleType = accountRoleTypeRepository.findByRoleType(RoleType.USER)
                .orElseThrow(() -> new IllegalStateException("RoleType.USER 이 DB에 없습니다."));

        Account account = new Account(accountRoleType);
        return accountRepository.save(account);
    }

    @Override
    public boolean authenticateAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("회원 인증 중 회원을 찾을 수 없습니다"));
        if (account == null) {
            return false;
        } else{
            return true;
        }
    }

    @Override
    public AccountResponseForm findById(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다"));
        
        // AccountProfile에서 nickname 조회
        String nickname = accountProfileService.getNicknameByAccountId(accountId);
        
        return new AccountResponseForm(accountId, nickname);
    }
    
    @Override
    public Account createGuestAccount(String email, String loginCode, String nickname) {
        // 이메일 중복 확인 (AccountProfile에서)
        if (accountProfileRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        
        // 게스트 계정 생성
        AccountRoleType accountRoleType = accountRoleTypeRepository.findByRoleType(RoleType.USER)
                .orElseThrow(() -> new IllegalStateException("RoleType.USER 이 DB에 없습니다."));
        
        Account account = new Account(accountRoleType, loginCode);
        Account savedAccount = accountRepository.save(account);
        
        // AccountProfile 생성
        AccountProfile profile = new AccountProfile(savedAccount, nickname, email);
        accountProfileRepository.save(profile);
        
        return savedAccount;
    }
    
    @Override
    @Transactional(readOnly = true)
    public Account loginGuest(String email, String loginCode) {
        // AccountProfile에서 이메일로 조회
        AccountProfile profile = accountProfileRepository.findWithAccountByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 로그인 코드가 일치하지 않습니다."));
        
        Account account = profile.getAccount();
        
        if (!loginCode.equals(account.getLoginCode())) {
            throw new IllegalArgumentException("이메일 또는 로그인 코드가 일치하지 않습니다.");
        }
        
        return account;
    }
    
    @Override
    public Account findByEmail(String email) {
        log.info("이메일로 계정 조회: {}", email);
        
        // AccountProfile에서 조회
        AccountProfile profile = accountProfileRepository.findWithAccountByEmail(email)
                .orElseThrow(() -> {
                    log.error("해당 이메일로 등록된 계정을 찾을 수 없음: {}", email);
                    return new IllegalArgumentException("해당 이메일로 등록된 계정을 찾을 수 없습니다: " + email);
                });
        
        log.info("account_profile에서 계정 찾음: accountId={}", profile.getAccount().getId());
        return profile.getAccount();
    }
    
    @Override
    @Transactional(readOnly = true)
    public AccountEmailResponse findByEmailWithDetails(String email) {
        log.info("이메일로 계정 상세 조회: {}", email);
        
        // AccountProfile에서 조회
        AccountProfile profile = accountProfileRepository.findWithAccountByEmail(email)
                .orElseThrow(() -> {
                    log.error("해당 이메일로 등록된 계정을 찾을 수 없음: {}", email);
                    return new IllegalArgumentException("해당 이메일로 등록된 계정을 찾을 수 없습니다: " + email);
                });
        
        log.info("account_profile에서 계정 찾음: accountId={}", profile.getAccount().getId());
        return new AccountEmailResponse(profile.getAccount().getId(), profile.getEmail());
    }
}
