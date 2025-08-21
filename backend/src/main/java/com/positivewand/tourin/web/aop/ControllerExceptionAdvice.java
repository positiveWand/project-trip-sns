package com.positivewand.tourin.web.aop;

import com.positivewand.tourin.web.exception.RedirectException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionAdvice {
    // Spring Security 예외 처리
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse("미인증", "인증이 필요하거나 인증에 실패했습니다."), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse("미인가", "요청을 처리할 수 없거나 자원에 접근할 수 없습니다."), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            MissingPathVariableException.class,
            MissingServletRequestPartException.class,
            MethodArgumentNotValidException.class,
            BindException.class,
            ConversionFailedException.class
    })
    public ResponseEntity<ErrorResponse> handleValidationException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse("잘못된 요청", e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler({
            NoSuchElementException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse("자원 없음", e.getMessage()), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler({
            IllegalArgumentException.class,
            IllegalStateException.class
    })
    public ResponseEntity<ErrorResponse> handleIllegalErrorException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse("처리 오류", e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    
    // 그 외 예외 처리
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUnknownException(Throwable e) {
        log.error("{}", e.getMessage(), e);
        return new ResponseEntity<>(new ErrorResponse("알 수 없는 서버 오류", "서버에서 알 수 없는 이유로 요청 처리에 실패했습니다."), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler
    public String handleRedirectException(RedirectException e) {
        return "redirect:" + e.getUri();
    }
}
