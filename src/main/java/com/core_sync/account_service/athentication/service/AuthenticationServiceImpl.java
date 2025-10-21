package com.core_sync.account_service.athentication.service;

import com.core_sync.account_service.account.service.AccountService;
import com.core_sync.account_service.redis_cache.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AccountService accountService;
    private final RedisCacheService redisCacheService;


    @Override
    public boolean authenticate(String token, Long accountId) {
        if(accountId == null){
            return false;
        }

        return accountService.authenticateAccount(accountId);
    }

    @Override
    public void logout(String token) {
        // Redis에서 토큰 삭제
        redisCacheService.deleteByKey(token);
    }
}
