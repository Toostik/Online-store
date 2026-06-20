package com.example.apigateway.controller.admin;

import com.example.apigateway.service.whitelist.WhitelistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/api/admin/whitelist")
@RequiredArgsConstructor
public class WhiteListController {

    private final WhitelistService whitelistService;

    @PostMapping("/users/{id}")
    public void addUser(
            @PathVariable Long id) {

        whitelistService.whitelistUser(
                id,
                Duration.ofDays(7)
        );

    }

    @DeleteMapping("/users/{id}")
    public void removeUser(
            @PathVariable Long id) {

        whitelistService.removeUser(id);
    }

    @PostMapping("/ip/{ip}")
    public void addUserIp(
            @PathVariable String ip) {

        whitelistService.whitelistIp(
                ip,
                Duration.ofDays(7)
        );

    }

    @DeleteMapping("/ip/{ip}")
    public void removeUserIp(
            @PathVariable String ip) {

        whitelistService.removeIp(ip);

    }

}
