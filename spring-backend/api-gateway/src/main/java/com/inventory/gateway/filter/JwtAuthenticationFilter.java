package com.inventory.gateway.filter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
@Component
public class JwtAuthenticationFilter extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
    public JwtAuthenticationFilter() {
        super(Config.class);
    }
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            String token = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            // TODO: Validate token using public key or secret from auth service.
            return chain.filter(exchange);
        };
    }
    public static class Config {
        // configuration parameters
    }
}
