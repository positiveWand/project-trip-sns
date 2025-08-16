package com.positivewand.tourin.web.common;

import com.positivewand.tourin.domain.auth.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@RequiredArgsConstructor
public class DefaultClientIdResolver implements ClientIdResolver {
    private final CustomUserDetailsService userDetailsService;

    @Override
    public String resolve() {
        String username = userDetailsService.getCurrentContextUser().getUsername();
        if (username.equals("anonymous"))
            return getClientIpAddr();

        return username;
    }

    private String getClientIpAddr() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = sra.getRequest();

        return request.getRemoteAddr();
    }
}
