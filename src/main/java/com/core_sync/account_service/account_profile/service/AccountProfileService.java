package com.core_sync.account_service.account_profile.service;


import com.core_sync.account_service.account.entity.Account;
import com.core_sync.account_service.account_profile.controller.response_form.AccountProfileResponseForm;
import com.core_sync.account_service.account_profile.entity.AccountProfile;
import com.core_sync.account_service.account_profile.service.request.RegisterAccountProfileRequest;

public interface AccountProfileService {
    AccountProfile createAccountProfile(Account account, RegisterAccountProfileRequest request);
    AccountProfileResponseForm findById(Long accountId);
    AccountProfile createGuestAccountProfile(Account account, String nickname, String email);
    String getNicknameByAccountId(Long accountId);
}
