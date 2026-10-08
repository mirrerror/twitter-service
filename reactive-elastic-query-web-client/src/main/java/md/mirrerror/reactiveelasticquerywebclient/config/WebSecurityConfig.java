package md.mirrerror.reactiveelasticquerywebclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    SecurityWebFilterChain webFluxSecurityConfig(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchange ->
                        exchange.anyExchange().permitAll())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

}