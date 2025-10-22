package com.core_sync.account_service.athentication.service;

public interface AuthenticationService {

    boolean authenticate(String token, Long accountId);
    
    void logout(String token);

}
