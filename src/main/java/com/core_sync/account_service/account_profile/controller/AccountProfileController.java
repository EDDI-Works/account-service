package com.core_sync.account_service.account_profile.controller;



import com.core_sync.account_service.account_profile.controller.response_form.AccountProfileResponseForm;
import com.core_sync.account_service.account_profile.repository.AccountProfileRepository;
import com.core_sync.account_service.account_profile.service.AccountProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/account-profile")
public class AccountProfileController {

    private final AccountProfileService accountProfileService;
    private final AccountProfileRepository accountProfileRepository;


    @GetMapping("/{accountId}")
    public AccountProfileResponseForm getAccountProfile(@PathVariable("accountId") Long accountId) {
        return accountProfileService.findById(accountId);
    }
    
    @GetMapping("/{accountId}/exists")
    public Boolean existsAccountProfile(@PathVariable("accountId") Long accountId) {
        return accountProfileRepository.findByAccountId(accountId) != null;
    }

}
