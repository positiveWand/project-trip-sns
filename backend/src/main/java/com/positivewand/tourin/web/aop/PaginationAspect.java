package com.positivewand.tourin.web.aop;

import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Aspect
@Component
public class PaginationAspect {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface PaginationHeader {
    }

    @Around("@annotation(com.positivewand.tourin.web.aop.PaginationAspect.PaginationHeader)")
    public Object attachPaginationHeader(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletResponse response = attributes.getResponse();
        Object result = joinPoint.proceed();

        if(response == null || !(result instanceof Page<?> page)) {
            return result;
        }

        response.setHeader("X-Pagination-Page", String.valueOf(page.getNumber()));
        response.setHeader("X-Pagination-Page-Size", String.valueOf(page.getSize()));
        response.setHeader("X-Pagination-Page-Limit", String.valueOf(page.getNumberOfElements()));
        response.setHeader("X-Pagination-Total-Page", String.valueOf(page.getTotalPages()));
        response.setHeader("X-Pagination-Total-Item", String.valueOf(page.getTotalElements()));

        return page;
    }
}
