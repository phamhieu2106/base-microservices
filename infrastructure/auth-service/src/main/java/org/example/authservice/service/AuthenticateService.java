package org.example.authservice.service;

import org.example.authservice.domain.request.AuthenticateRequest;
import org.example.sharedlibrary.response.WrapperResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

public interface AuthenticateService {
    WrapperResponse register(AuthenticateRequest authRequest);

    WrapperResponse authenticate(AuthenticateRequest authRequest);

    WrapperResponse registerAdmin();

    ResponseEntity<?> validateToken(String token);

    String extractUsername(String token);

    boolean isTokenValid(String token, UserDetails userDetails);
}
