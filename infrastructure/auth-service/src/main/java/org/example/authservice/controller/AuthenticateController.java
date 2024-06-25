package org.example.authservice.controller;

import lombok.RequiredArgsConstructor;
import org.example.authservice.domain.request.AuthenticateRequest;
import org.example.authservice.service.AuthenticateService;
import org.example.sharedlibrary.response.WrapperResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/v1")
@RequiredArgsConstructor
public class AuthenticateController {

    private final AuthenticateService authenticateService;

    @PostMapping("/register-admin")
    public WrapperResponse registerAdmin() {
        return authenticateService.registerAdmin();
    }

    @PostMapping("/login")
    public WrapperResponse login(@RequestBody AuthenticateRequest authRequest) {
        return authenticateService.authenticate(authRequest);
    }

    @PostMapping("/register")
    public WrapperResponse register(@RequestBody AuthenticateRequest authRequest) {
        return authenticateService.register(authRequest);
    }

}
