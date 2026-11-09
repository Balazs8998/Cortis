package com.cortis.core.security.config;

import com.cortis.auth.security.ChipCardAuthenticationProvider;
import com.cortis.auth.security.CortisUserDetailsService;
import com.cortis.core.security.audit.SecurityAuditService;
import com.cortis.core.security.ratelimit.RateLimitFilter;
import com.cortis.core.security.ratelimit.RateLimitKeyResolver;
import com.cortis.core.security.ratelimit.RateLimitService;
import com.cortis.core.security.session.CortisAccessDeniedHandler;
import com.cortis.core.security.session.CortisAuthenticationEntryPoint;
import com.cortis.core.security.session.SessionAccountValidationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfAuthenticationStrategy;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            CortisUserDetailsService userDetailsService,
            ChipCardAuthenticationProvider chipCardAuthenticationProvider,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider dao = new DaoAuthenticationProvider(userDetailsService);
        dao.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(List.of(dao, chipCardAuthenticationProvider));
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public CookieCsrfTokenRepository cookieCsrfTokenRepository(
            @Value("${security.session.secure-csrf-cookie:false}") boolean secure
    ) {
        CookieCsrfTokenRepository repository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        repository.setCookieCustomizer(cookie -> cookie.path("/").sameSite("Lax").secure(secure));
        return repository;
    }

    // Custom JSON login controllers bypass UsernamePasswordAuthenticationFilter.
    // Apply the normal session-fixation and CSRF rotation strategies explicitly.
    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy(
            CookieCsrfTokenRepository csrfTokenRepository
    ) {
        return new CompositeSessionAuthenticationStrategy(List.of(
                new ChangeSessionIdAuthenticationStrategy(),
                new CsrfAuthenticationStrategy(csrfTokenRepository)
        ));
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            SecurityContextRepository securityContextRepository,
            CookieCsrfTokenRepository csrfTokenRepository,
            CortisAuthenticationEntryPoint entryPoint,
            CortisAccessDeniedHandler deniedHandler,
            RateLimitKeyResolver rateLimitKeyResolver,
            RateLimitService rateLimitService,
            SecurityAuditService auditService,
            CortisUserDetailsService userDetailsService
    ) throws Exception {
        RateLimitFilter rateLimitFilter = new RateLimitFilter(rateLimitService, rateLimitKeyResolver);
        SessionAccountValidationFilter sessionAccountValidationFilter =
                new SessionAccountValidationFilter(userDetailsService, entryPoint, auditService);

        http
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .requestCache(AbstractHttpConfigurer::disable)
            .csrf(csrf -> csrf
                .csrfTokenRepository(csrfTokenRepository)
                // SPA sends the plain X-XSRF-TOKEN header; API does not render HTML tokens.
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
            )
            .securityContext(context -> context
                .securityContextRepository(securityContextRepository)
                .requireExplicitSave(true)
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionFixation(fixation -> fixation.changeSessionId())
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.GET, "/api/auth/csrf").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/loginWithChip").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/translation/{languageCode}").permitAll()
                .requestMatchers(HttpMethod.GET, "/actuator/health", "/actuator/info").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(handler -> handler
                .authenticationEntryPoint(entryPoint)
                .accessDeniedHandler(deniedHandler)
            )
            .logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("CORTIS_SESSION", "XSRF-TOKEN")
                .logoutSuccessHandler((request, response, authentication) -> {
                    auditService.record("LOGOUT", "SUCCESS", userId(authentication), request);
                    response.setStatus(204);
                })
            )
            // Placed after SecurityContextHolderFilter: user identity is available.
            .addFilterBefore(sessionAccountValidationFilter, AuthorizationFilter.class)
            .addFilterBefore(rateLimitFilter, AuthorizationFilter.class);
        return http.build();
    }

    private java.util.UUID userId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof
                com.cortis.auth.security.CortisUserPrincipal principal) {
            return principal.getUserId();
        }
        return null;
    }
}
