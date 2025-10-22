package com.core_sync.account_service.dashboard.controller;

import com.core_sync.account_service.redis_cache.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/account/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    
    private final RedisCacheService redisCacheService;
    private final RestTemplate restTemplate;
    
    @Value("${hr.service.url:http://localhost:8003}")
    private String hrServiceUrl;
    
    // 사용자의 팀 통계 조회 (HR Service에서 가져옴)
    @GetMapping("/team-stats")
    public ResponseEntity<Map<String, Object>> getTeamStats(
            @RequestHeader("Authorization") String token
    ) {
        String userToken = token.replace("Bearer ", "").trim();
        Long accountId = redisCacheService.getValueByKey(userToken, Long.class);
        
        if (accountId == null) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
        
        try {
            // HR Service에 팀 목록 요청
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + userToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            log.info("HR Service 호출 - URL: {}, accountId: {}", hrServiceUrl + "/api/team/list", accountId);
            
            ResponseEntity<java.util.List> hrResponse = restTemplate.exchange(
                hrServiceUrl + "/api/team/list",
                HttpMethod.GET,
                entity,
                java.util.List.class
            );
            
            java.util.List<?> teams = hrResponse.getBody();
            log.info("HR Service 응답 - teams: {}", teams);
            int teamCount = teams != null ? teams.size() : 0;
            
            Map<String, Object> response = new HashMap<>();
            response.put("teamCount", teamCount);
            
            log.info("팀 통계 조회 - accountId: {}, teamCount: {}", accountId, teamCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("HR Service 호출 실패", e);
            Map<String, Object> response = new HashMap<>();
            response.put("teamCount", 0);
            return ResponseEntity.ok(response);
        }
    }
}
