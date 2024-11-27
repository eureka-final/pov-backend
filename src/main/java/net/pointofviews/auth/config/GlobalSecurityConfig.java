package net.pointofviews.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
@RequiredArgsConstructor
public class GlobalSecurityConfig {

    private final CorsConfig cors;

    @Bean
    public SecurityFilterChain globalSecurityFilterChain(HttpSecurity http) throws Exception {
        final RequestMatcher ignoredRequests = ignoredRequests();

        http.cors(config -> config.configurationSource(cors.corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ignoredRequests).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    private RequestMatcher ignoredRequests() {
        return new OrRequestMatcher(
                new AntPathRequestMatcher("/error"),
                new AntPathRequestMatcher("/actuator/**"),
                new AntPathRequestMatcher("/auth/**"),
                PathRequest.toStaticResources().atCommonLocations()
        );
    }
}