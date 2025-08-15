package com.positivewand.tourin.web.page;

import com.positivewand.tourin.domain.auth.CustomUserDetailsService;
import com.positivewand.tourin.web.exception.RedirectException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PageController {
    private final CustomUserDetailsService userDetailsService;

    @GetMapping({"/my"})
    public ResponseEntity<Resource> serveMyPage() {
        Resource resource = new ClassPathResource("static/my/index.html");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }
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
            String username = userDetailsService.getCurrentContextUser().getUsername();
            throw new RedirectException("/social/user/"+username);
        }

        Resource resource = new ClassPathResource("static/social/index.html");

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(resource);
    }
}
