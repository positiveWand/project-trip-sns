package com.positivewand.tourin.web.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogTraceAspect {
    public final static ThreadLocal<String> requestIdHolder = new ThreadLocal<>();
    public final static ThreadLocal<String> jsessionIdHolder = new ThreadLocal<>();

    @Around("execution(* com.positivewand.tourin..*Controller.*(..)) || execution(* com.positivewand.tourin..*Service.*(..))")
    public Object logTrace(ProceedingJoinPoint joinPoint) throws Throwable {
        String requestId = LogTraceAspect.requestIdHolder.get();
        String jsessionId = LogTraceAspect.jsessionIdHolder.get();
        if (requestId == null) {
            requestId = "ANONYMOUS";
        }
        if (jsessionId == null) {
            jsessionId = "NONE";
        }

        try {
            log.trace("[Request ID: {}, JSESSIONID: {}] 진입 - {} args={}", requestId, jsessionId, joinPoint.getSignature(), joinPoint.getArgs());
            Object result = joinPoint.proceed();
            log.trace("[Request ID: {}, JSESSIONID: {}] 탈출 - {} return={}", requestId, jsessionId, joinPoint.getSignature(), result);
            return result;
        } catch(Throwable e) {
            log.error("[Request ID: {}, JSESSIONID: {}] 예외 - {} exception={}", requestId, jsessionId, joinPoint.getSignature(), e.toString());
            throw e;
        }
    }
}
