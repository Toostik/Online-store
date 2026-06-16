package com.example.cartservice.service.security;


import com.example.cartservice.exceptions.user.UnauthorizedUserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {

    public Long getCurrentUserId(){
        log.debug("GET_AUTH_USER");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getName() == null) {
            log.error("UNAUTHORIZED_USER");
            throw new UnauthorizedUserException("Unauthorized");
        }
        log.debug("AUTH_USER_ID {}", auth.getName());
        return Long.parseLong(auth.getName());

    }

}
