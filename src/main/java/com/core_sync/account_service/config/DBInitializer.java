package com.core_sync.account_service.config;

import com.core_sync.account_service.account.entity.AccountRoleType;
import com.core_sync.account_service.account.entity.RoleType;
import com.core_sync.account_service.account.repository.AccountRoleTypeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Component
public class DBInitializer {

    private final AccountRoleTypeRepository accountRoleTypeRepository;

    @PostConstruct
    private void init () {
        log.debug("initializer 시작!");

        initAccountRoleTypes();

        log.debug("initializer 종료!");
    }

    private void initAccountRoleTypes() {
        try {
            final Set<RoleType> roles =
                    accountRoleTypeRepository.findAll().stream()
                            .map(AccountRoleType::getRoleType)
                            .collect(Collectors.toSet());

            for (RoleType type: RoleType.values()) {
                if (!roles.contains(type)) {
                    final AccountRoleType role = new AccountRoleType(type);
                    accountRoleTypeRepository.save(role);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
