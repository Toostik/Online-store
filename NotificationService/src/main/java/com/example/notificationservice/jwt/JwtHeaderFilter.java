package com.example.notificationservice.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class JwtHeaderFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String rolesHeader = request.getHeader("X-User-Role");
        if(rolesHeader != null){
            List<SimpleGrantedAuthority> authorities =
                    Arrays.stream(rolesHeader.split(","))
                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                            .toList();
            Authentication auth = new UsernamePasswordAuthenticationToken(null, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
       filterChain.doFilter(request, response);
    }
}
