package org.example.authservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.authservice.domain.entity.UserEntity;
import org.example.authservice.domain.request.AuthenticateRequest;
import org.example.authservice.domain.response.AuthenticateResponse;
import org.example.authservice.enumeration.UserRole;
import org.example.authservice.es.event.UserCreatedEvent;
import org.example.authservice.es.service.EventProducerService;
import org.example.authservice.repository.UserRepository;
import org.example.authservice.service.AuthenticateService;
import org.example.sharedlibrary.response.WrapperResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthenticateServiceImpl implements AuthenticateService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EventProducerService eventProducer;
    private final ObjectMapper objectMapper;
    @Value("${JWT_SECRET_KEY}")
    private String SECRET_KEY;

    @Override
    public WrapperResponse register(AuthenticateRequest authRequest) {
        if (authRequest == null
                || authRequest.getUsername() == null
                || authRequest.getPassword() == null
                || authRequest.getUsername().isBlank() || authRequest.getPassword().isBlank()) {
            return WrapperResponse.returnResponse(
                    false, "Invalid Username or Password!"
                    , null, HttpStatus.BAD_REQUEST
            );
        }
        if (userRepository.existsByUsername(authRequest.getUsername())) {
            return WrapperResponse.returnResponse(
                    false, "Username Already Exits!", null, HttpStatus.CONFLICT
            );
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(authRequest.getUsername());
        userEntity.setPassword(passwordEncoder.encode(authRequest.getPassword()));
        userEntity.setRole(UserRole.USER);
        userEntity.setUserCode(generateUserCode(userRepository.count()));
        userEntity.setCreatedAt(new Date());
        UserEntity userEvent = userRepository.save(userEntity);

        UserCreatedEvent event = new UserCreatedEvent(userEvent.getId());
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            eventProducer.sendEvent(eventJson);
        } catch (Exception e) {
            return WrapperResponse.returnResponse(
                    false, "Fail When Trying Store Event!"
                    , null, HttpStatus.INTERNAL_SERVER_ERROR
            );
        }

        return WrapperResponse.returnResponse(
                true, "Register Successfully!", null, HttpStatus.OK
        );
    }


    @Override
    public WrapperResponse authenticate(AuthenticateRequest authRequest) {
        if (authRequest == null
                || authRequest.getUsername() == null
                || authRequest.getPassword() == null
                || authRequest.getUsername().isBlank() || authRequest.getPassword().isBlank()) {
            return WrapperResponse.returnResponse(
                    false, "Invalid Username or Password!"
                    , null, HttpStatus.BAD_REQUEST
            );
        }

        Optional<UserEntity> optionalUser = userRepository
                .findUserByUsernameAndSoftDeleteIsFalse(authRequest.getUsername());

        if (optionalUser.isEmpty()
                || !passwordEncoder.matches(authRequest.getPassword(), optionalUser.get().getPassword())) {

            return WrapperResponse.returnResponse(
                    false, "Username Or Password Not Right!", null, HttpStatus.BAD_REQUEST
            );
        }

        AuthenticateResponse response = new AuthenticateResponse();
        response.setToken(getToken(optionalUser.get()));

        return WrapperResponse.returnResponse(
                true, "Authenticate Successfully!", response, HttpStatus.OK
        );
    }

    @Override
    public WrapperResponse registerAdmin() {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername("henry2106");
        userEntity.setPassword(passwordEncoder.encode("123"));
        userEntity.setRole(UserRole.ADMIN);
        userEntity.setUserCode(generateUserCode(userRepository.count()));
        userEntity.setCreatedAt(new Date());
        userRepository.save(userEntity);
        return WrapperResponse.returnResponse(
                true, "Register Admin Successfully!", null, HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<?> validateToken(String token) {
        try {
            String jwt = token.substring(7);
            String userUsername = extractUsername(jwt);
            if (userUsername != null) {
                Optional<UserEntity> userDetails = userRepository.findUserByUsernameAndSoftDeleteIsFalse(userUsername);
                return ResponseEntity.ok(userDetails.filter(userEntity -> isTokenValid(jwt, userEntity)).isPresent());
            }
        } catch (ExpiredJwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token expired");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private boolean isTokenExpired(String token) {
//        Is Expired ?
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
//        Get time Expire
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String generateUserCode(long count) {
        String userCode = String.format("U%03d", count);

        do {
            if (this.userRepository.existsByUserCode(userCode)) {
                userCode = String.format("C%03d", ++count);
                return userCode;
            }
        } while (this.userRepository.existsByUserCode(userCode));

        return userCode;
    }

    private String getToken(UserDetails userDetails) {
        return generateToken(userDetails);
    }

    private String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
