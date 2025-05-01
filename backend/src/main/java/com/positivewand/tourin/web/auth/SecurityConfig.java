package com.positivewand.tourin.web.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    public static final String[] pageUrlPattern = {
            "/",
            "/index.html",
            "/map",
            "/map/**",
            "/social",
            "/social/**",
            "/login",
            "/signup",
            "/my",
            "/assets/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AccessDeniedHandler accessDeniedHandler,
            AuthenticationEntryPoint authenticationEntryPoint,
            SecurityContextRepository securityContextRepository
    ) throws Exception {

        http    .csrf(csrf -> csrf.disable())
                .securityContext(securityContext -> securityContext.securityContextRepository(securityContextRepository))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(pageUrlPattern).permitAll()
                        .requestMatchers("/api/auth/signup", "/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/users/{userId}/bookmarks").access(
                                (authenticationSupplier, context) -> {
                                    Authentication auth = authenticationSupplier.get();
                                    Map<String, String> pathVariables = context.getVariables();

                                    if(auth == null || !auth.isAuthenticated()) {
                                        throw new InsufficientAuthenticationException("인증이 필요합니다.");
                                    }

                                    if(!auth.getName().equals(pathVariables.get("userId"))) {
                                        return new AuthorizationDecision(false);
                                    }

                                    return new AuthorizationDecision(true);
                                }
                        )
                        .requestMatchers(HttpMethod.DELETE, "/api/users/{userId}/bookmarks/{tourSpotId}").access(
                                (authenticationSupplier, context) -> {
                                    Authentication auth = authenticationSupplier.get();
                                    Map<String, String> pathVariables = context.getVariables();

                                    if(auth == null || !auth.isAuthenticated()) {
                                        throw new InsufficientAuthenticationException("인증이 필요합니다.");
                                    }

                                    if(!auth.getName().equals(pathVariables.get("userId"))) {
                                        return new AuthorizationDecision(false);
                                    }

                                    return new AuthorizationDecision(true);
                                }
                        )
                        .requestMatchers(HttpMethod.POST, "/api/users/{userId}/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/users/{userId}/**").authenticated()
                        .requestMatchers("/api/users", "/api/users/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/tour-spots", "/api/tour-spots/**").permitAll()
                        .requestMatchers("/api/tour-spots/**", "/api/tour-spot-reviews/**").authenticated()
                        .requestMatchers("/api/recommendations/**").authenticated()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .sessionManagement(session -> session.sessionCreationPolicy((SessionCreationPolicy.IF_REQUIRED)));

        return http.build();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }
    @Bean
    public SecurityContextLogoutHandler securityContextLogoutHandler() {
        return new SecurityContextLogoutHandler();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        return new CustomAccessDeniedHandler(resolver);
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(@Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        return new CustomAuthenticationEntryPoint(resolver);
    }
}
