package tn.iteam.jobservice.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final String ROLE_RH    = "ROLE_RH";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    @Order(1)
    SecurityFilterChain swaggerSecurityFilterChain(HttpSecurity http) throws Exception {
        return http
                .securityMatcher(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**"
                )
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Job offers consultation: public (offres publiées) + interne sans auth
                        .requestMatchers(HttpMethod.GET, "/api/jobs/**").permitAll()
                        // Job offers CRUD: RH/Admin only
                        .requestMatchers("/api/admin/jobs/**").hasAnyAuthority(ROLE_ADMIN, ROLE_RH)
                        // Referrals: all authenticated
                        .requestMatchers("/api/referrals/**").hasAnyAuthority("ROLE_EMPLOYE", ROLE_RH, ROLE_ADMIN)
                        // Audit: RH/ADMIN only
                        .requestMatchers("/api/audit/**").hasAnyAuthority(ROLE_RH, ROLE_ADMIN)
                        // HR Metrics
                        .requestMatchers("/api/hr/**").hasAnyAuthority(ROLE_RH, ROLE_ADMIN)
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
