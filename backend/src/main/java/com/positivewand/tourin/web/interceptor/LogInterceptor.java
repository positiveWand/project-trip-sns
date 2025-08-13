package com.positivewand.tourin.web.interceptor;

import com.positivewand.tourin.domain.auth.CustomUserDetails;
import com.positivewand.tourin.domain.auth.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.util.UUID;

import static net.logstash.logback.argument.StructuredArguments.value;


@Slf4j
@Component
@RequiredArgsConstructor
public class LogInterceptor implements HandlerInterceptor {
    private static final String START_TIME_ATTR = "logStartTime";
    private static final ThreadLocal<Long> requestStartTime = new ThreadLocal<>();

    private final CustomUserDetailsService customUserDetailsService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        requestStartTime.set(System.currentTimeMillis());
        CustomUserDetails user = customUserDetailsService.getCurrentContextUser();

        MDC.put("traceId", UUID.randomUUID().toString());
        MDC.put("user", user != null ? user.getUsername() : null);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 요청 로그
        long duration = (requestStartTime.get() != null) ? (System.currentTimeMillis() - requestStartTime.get()) : -1;

        log.info("{} {} {} {} {}ms {}",
                value("remoteIp", request.getRemoteAddr()), // 프록시를 고려하지 않음 -> 해볼까?
                value("httpMethod", request.getMethod()),
                value("uri", request.getRequestURI()),
                value("httpStatus", response.getStatus()),
                value("duration", duration),
                value("userAgent", request.getHeader("User-Agent") != null ?
                        "\"" + request.getHeader("User-Agent") + "\"" :
                        null
                )
        );

        MDC.clear();
    }
}
