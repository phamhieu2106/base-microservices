package org.example.authservice.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.example.authservice.service.AuthenticateService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticateService authenticateService;
    private final UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request
            , @NonNull HttpServletResponse response
            , @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String autHeader = request.getHeader("Authorization");
        final String jwt;
        final String userUsername;
        if (autHeader == null || !autHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = autHeader.substring(7);
        try {
            userUsername = authenticateService.extractUsername(jwt);
            if (userUsername != null && SecurityContextHolder
                    .getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userUsername);

                if (authenticateService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
//                GetContext
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                }
            }
        } catch (ExpiredJwtException expiredJwtException) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token expired");
            return;
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
