package org.example.apigateway.config;

import org.example.apigateway.filter.AuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final AuthFilter authFilter;

    @Autowired
    public GatewayConfig(AuthFilter authFilter) {
        this.authFilter = authFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth", r -> r.path("/auth/v1/**")
                        .uri("lb://AUTH-SERVICE"))
                .route("customer", r -> r.path("/api/customers/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://CUSTOMER-SERVICE"))
                .route("insurance", r -> r.path("/api/insurances/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://INSURANCE-SERVICE"))
                .route("contract", r -> r.path("/api/contracts/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://CONTRACT-SERVICE"))
                .build();
    }
}
