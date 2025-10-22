package com.core_sync.account_service.account.service;


import com.core_sync.account_service.account.controller.response_form.AccountResponseForm;
import com.core_sync.account_service.account.entity.Account;
import com.core_sync.account_service.account.service.request.RegisterNormalAccountRequest;
import com.core_sync.account_service.account.service.response.AccountEmailResponse;

public interface AccountService {
    Account createAccount(RegisterNormalAccountRequest request);
    boolean authenticateAccount(Long accountId);
    AccountResponseForm findById(Long accountId);
    Account createGuestAccount(String email, String loginCode, String nickname);
    Account loginGuest(String email, String loginCode);
    Account findByEmail(String email);
    AccountEmailResponse findByEmailWithDetails(String email);
}
