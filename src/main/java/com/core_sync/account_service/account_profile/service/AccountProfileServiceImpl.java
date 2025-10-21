package com.core_sync.account_service.account_profile.service;
import com.core_sync.account_service.account.entity.Account;
import com.core_sync.account_service.account_profile.controller.response_form.AccountProfileResponseForm;
import com.core_sync.account_service.account_profile.entity.AccountProfile;
import com.core_sync.account_service.account_profile.entity.Email;
import com.core_sync.account_service.account_profile.entity.Nickname;
import com.core_sync.account_service.account_profile.repository.AccountProfileRepository;
import com.core_sync.account_service.account_profile.service.request.RegisterAccountProfileRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountProfileServiceImpl implements AccountProfileService {
    final private AccountProfileRepository accountProfileRepository;

    @Override
    public AccountProfile createAccountProfile(Account account, RegisterAccountProfileRequest request) {

        Email email = new Email(request.getEmail());
        Nickname nickname = new Nickname(request.getNickname());

        if (accountProfileRepository.existsByEmail(email.getValue())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if (accountProfileRepository.existsByNickname(nickname.getValue())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        AccountProfile accountProfile = new AccountProfile(account, nickname.getValue(), email.getValue());
        return accountProfileRepository.save(accountProfile);
    }


    @Override
    public AccountProfileResponseForm findById(Long accountId) {
        AccountProfile accountProfile = accountProfileRepository.findByAccountId(accountId);
        return new AccountProfileResponseForm(accountProfile.getId(), accountId, accountProfile.getNickname(), accountProfile.getEmail());
    }
    
    @Override
    public AccountProfile createGuestAccountProfile(Account account, String nickname, String email) {
        Nickname nicknameObj = new Nickname(nickname);
        
        if (accountProfileRepository.existsByNickname(nicknameObj.getValue())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        
        // 이메일을 파라미터로 받아서 사용
        AccountProfile accountProfile = new AccountProfile(account, nicknameObj.getValue(), email);
        return accountProfileRepository.save(accountProfile);
    }
    
    @Override
    public String getNicknameByAccountId(Long accountId) {
        AccountProfile accountProfile = accountProfileRepository.findByAccountId(accountId);
        if (accountProfile == null) {
            return "User";
        }
        return accountProfile.getNickname();
    }

}
