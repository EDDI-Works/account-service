package com.core_sync.account_service.athentication.controller;

import com.core_sync.account_service.athentication.service.AuthenticationService;
import com.core_sync.account_service.redis_cache.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/authentication")
public class AuthenticationController {

    private final RedisCacheService redisCacheService;
    private final AuthenticationService authenticationService;

    @GetMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestHeader("AuthenticationHeader") String authenticationHeader) {

        String userToken = authenticationHeader.replace("Bearer ", "").trim();
        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);

        boolean authenticateResult = authenticationService.authenticate(userToken, accountId);

        if(authenticateResult){
            return ResponseEntity.ok().build();
        } else{
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authorizationHeader) {
        try {
            String userToken = authorizationHeader.replace("Bearer ", "").trim();
            
            log.info("로그아웃 요청 - 토큰: {}", userToken.substring(0, Math.min(10, userToken.length())) + "...");
            
            // Redis에서 토큰 삭제
            authenticationService.logout(userToken);
            
            log.info("로그아웃 성공");
            return ResponseEntity.ok().body("로그아웃 성공");
        } catch (Exception e) {
            log.error("로그아웃 실패", e);
            return ResponseEntity.internalServerError().body("로그아웃 실패");
        }
    }


}
