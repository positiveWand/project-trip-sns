package com.positivewand.tourin.web.page;

import com.positivewand.tourin.domain.auth.CustomUserDetails;
import com.positivewand.tourin.web.exception.RedirectException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PageController {
    @GetMapping({"/login"})
    public ResponseEntity<Resource> serveLoginPage() {
        Resource resource = new ClassPathResource("static/login/index.html");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }
    @GetMapping({"/signup"})
    public ResponseEntity<Resource> serveSignupPage() {
        Resource resource = new ClassPathResource("static/signup/index.html");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }
    @GetMapping({"/map", "/map/**"})
    public ResponseEntity<Resource> serveMapPage() {
        Resource resource = new ClassPathResource("static/map/index.html");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }

    @GetMapping({"/social", "/social/**"})
    public ResponseEntity<Resource> serveSocialPage(HttpServletRequest request) {
        if(request.getRequestURI().equals("/social")) {
            SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
            SecurityContext context = securityContextHolderStrategy.getContext();
            String username = ((CustomUserDetails) context.getAuthentication().getPrincipal()).getUsername();
            throw new RedirectException("/social/user/"+username);
        }

        Resource resource = new ClassPathResource("static/social/index.html");

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }
}
