package com.positivewand.tourin.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogAspect {
    @Around("execution(* com.positivewand.tourin..*Controller.*(..)) || execution(* com.positivewand.tourin..*Service.*(..))")
    public Object traceMethodCall(ProceedingJoinPoint joinPoint) throws Throwable {
        long callTime = System.currentTimeMillis();
        long returnTime;
        try {
            log.trace("메소드 진입(CALL_TIME+0ms) - {} args={}", joinPoint.getSignature(), joinPoint.getArgs());
            Object result = joinPoint.proceed();
            returnTime = System.currentTimeMillis();
            log.trace("메소드 탈출(CALL_TIME+{}ms) - {} return={}", returnTime-callTime, joinPoint.getSignature(), result);

            return result;
        } catch(Throwable e) {
            returnTime = System.currentTimeMillis();
            if(log.isTraceEnabled())
                log.error("메소드 예외(CALL_TIME+{}ms) - {}", returnTime-callTime, joinPoint.getSignature(), e);

            throw e;
        }
    }
}
