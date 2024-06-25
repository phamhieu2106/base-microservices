package org.example.authservice.controller.internal;

import lombok.RequiredArgsConstructor;
import org.example.authservice.service.AuthenticateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/v1/internal")
@RequiredArgsConstructor
public class AuthenticateInternalController {

    private final AuthenticateService authenticateService;

    @PostMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        return this.authenticateService.validateToken(token);
    }
}
