package com.positivewand.tourin.web.aop;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Slf4j
public class LogTraceInitFilter extends OncePerRequestFilter {
    public final static ThreadLocal<String> requestIdHolder = new ThreadLocal<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestId = UUID.randomUUID().toString();
        String jsessionId = getCookie(request.getCookies(), "JSESSIONID");

        LogTraceInitFilter.requestIdHolder.set(requestId);
        try {
            log.trace("[Request ID: {}] 요청 수신, JSESSIONID: {}", requestId, jsessionId);
            filterChain.doFilter(request, response);
        } finally {
            LogTraceInitFilter.requestIdHolder.remove();
        }
    }

    private static String getCookie(Cookie[] cookies, String cookieName) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null; // 쿠키가 없으면 null 반환
    }
}
