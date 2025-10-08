package com.positivewand.tourin.web.security;

import com.positivewand.tourin.domain.auth.CustomUserDetails;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Arrays;

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
            SecurityContextRepository securityContextRepository,
            UrlBasedCorsConfigurationSource corsConfigurationSource
    ) throws Exception {

        http    .csrf(csrf -> csrf.disable())
                .securityContext(securityContext -> securityContext.securityContextRepository(securityContextRepository))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(pageUrlPattern).permitAll()
                        .requestMatchers("/api/auth/signup", "/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/**").authenticated()
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
                .anonymous(anonymous -> anonymous.principal(CustomUserDetails.createAnonymousUser()))
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
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

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:[5173,8080]"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "DELETE", "PUT", "OPTIONS", "HEAD"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(Arrays.asList("X-Pagination-Page", "X-Pagination-Page-Size", "X-Pagination-Page-Limit", "X-Pagination-Total-Page", "X-Pagination-Total-Item", "Authorization", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("X-Pagination-Page", "X-Pagination-Page-Size", "X-Pagination-Page-Limit", "X-Pagination-Total-Page", "X-Pagination-Total-Item", "Authorization", "Content-Type"));
//        configuration.setAllowedHeaders(Arrays.asList("*"));
//        configuration.setExposedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
