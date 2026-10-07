package md.mirrerror.reactiveelasticqueryservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class WebSecurityConfig {

    SecurityWebFilterChain webFluxSecurityConfig(ServerHttpSecurity http) {
        return http
                .authorizeExchange(exchangeSpec ->
                        exchangeSpec.anyExchange().permitAll())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

}
