package com.positivewand.tourin.web.aop;

import com.positivewand.tourin.domain.user.entity.User;
import com.positivewand.tourin.domain.user.exception.DuplicateUserException;
import com.positivewand.tourin.domain.user.exception.NoSuchUserException;
import com.positivewand.tourin.web.exception.RedirectException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class ControllerExceptionAdvice {
    // Spring Security 예외 처리
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(RuntimeException e) {
        return new ResponseEntity<>(new ErrorResponse("미인증", e.getMessage()), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(RuntimeException e) {
        return new ResponseEntity<>(new ErrorResponse("미인가", e.getMessage()), HttpStatus.FORBIDDEN);
    }
    
    // Validation 예외 처리
    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorResponse> handleValidationException(RuntimeException e) {
        return new ResponseEntity<>(new ErrorResponse("파라미터 유효하지 않음", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    // 서비스 예외 처리
    @ExceptionHandler({
            NoSuchElementException.class,
            NoSuchUserException.class,
            DuplicateUserException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFoundException(RuntimeException e) {
        return new ResponseEntity<>(new ErrorResponse("자원 부재", e.getMessage()), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler({
            User.InvalidUserEntity.class,
    })
    public ResponseEntity<ErrorResponse> handleBadRequestException(RuntimeException e) {
        return new ResponseEntity<>(new ErrorResponse("잘못된 요청", e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    
    // 그 외 예외 처리
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUnknownRuntimeException(RuntimeException e) {
        return new ResponseEntity<>(new ErrorResponse("알 수 없는 서버 오류", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler
    public String handleRedirectException(RedirectException e) {
        return "redirect:" + e.getUri();
    }
}
