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
    @Around("execution(* com.positivewand.tourin..*Controller.*(..)) || execution(* com.positivewand.tourin..*Service.*(..))")
    public Object traceMethodCall(ProceedingJoinPoint joinPoint) throws Throwable {
        String requestId = LogTraceInitFilter.requestIdHolder.get();
        if (requestId == null) {
            requestId = "NO_REQUEST_ID";
        }

        long callTime = System.currentTimeMillis();
        long returnTime;

        try {
            log.trace("[Request ID: {}] 진입 - {} args={}", requestId, joinPoint.getSignature(), joinPoint.getArgs());
            Object result = joinPoint.proceed();
            returnTime = System.currentTimeMillis();
            log.trace("[Request ID: {}] 탈출(CALL_TIME+{}ms) - {} return={}", requestId, returnTime-callTime, joinPoint.getSignature(), result);

            return result;
        } catch(Throwable e) {
            returnTime = System.currentTimeMillis();
            log.error("[Request ID: {}] 예외(CALL_TIME+{}ms) - {} exception={}", requestId, returnTime-callTime, joinPoint.getSignature(), e.toString());

            throw e;
        }
    }
}
