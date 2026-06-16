package com.example.userservice.service.security;

import com.example.userservice.exceptions.UnauthorizedUserException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SecurityService {
    public Long getCurrentUserId(){
        log.debug("GET_AUTHENTICATED_USER");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            log.error("UNAUTHORIZED_ACCESS");
            throw new UnauthorizedUserException("Unauthorized");
        }
        log.debug("AUTH_USER_ID id={}", auth.getName());
        return Long.parseLong(auth.getName());
    }
}
