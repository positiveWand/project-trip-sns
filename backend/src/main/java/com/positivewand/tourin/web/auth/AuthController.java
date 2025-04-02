package com.positivewand.tourin.web.auth;

import com.positivewand.tourin.domain.auth.CustomUserDetails;
import com.positivewand.tourin.domain.auth.CustomUserDetailsService;
import com.positivewand.tourin.domain.user.UserService;
import com.positivewand.tourin.web.auth.request.UpdatePasswordRequest;
import com.positivewand.tourin.web.auth.request.LoginRequest;
import com.positivewand.tourin.web.auth.request.SignupRequest;
import com.positivewand.tourin.web.auth.request.UpdateProfileRequest;
import com.positivewand.tourin.web.auth.response.UserResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final SecurityContextRepository securityContextRepository;
    private final SecurityContextLogoutHandler securityContextLogoutHandler;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse signup(@RequestBody SignupRequest signupRequest) {
        userService.createUser(
                signupRequest.id(),
                signupRequest.password(),
                signupRequest.name(),
                signupRequest.email()
        );

        return new UserResponse(signupRequest.id(), signupRequest.name(), signupRequest.email());
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse login(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Authentication auth = UsernamePasswordAuthenticationToken.unauthenticated(
                loginRequest.id(),
                loginRequest.password()
        );
        auth = this.authenticationManager.authenticate(auth);

        // Security Context 저장
        // 참조 - https://docs.spring.io/spring-security/reference/servlet/authentication/session-management.html#store-authentication-manually
        SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(auth);
        securityContextHolderStrategy.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        return new UserResponse(user.getUsername(), user.getName(), user.getEmail());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        // Security Context 초기화
        // 참조 - https://docs.spring.io/spring-security/reference/servlet/authentication/logout.html#creating-custom-logout-endpoint
        securityContextLogoutHandler.logout(request, response, null);

        return "redirect:/";
    }

    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse me() {
        CustomUserDetails user = customUserDetailsService.getCurrentContextUser();

        return new UserResponse(user.getUsername(), user.getName(), user.getEmail());
    }

    @PostMapping("/delete-account")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public String deleteAccount(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        CustomUserDetails user = customUserDetailsService.getCurrentContextUser();

        userService.deleteUser(user.getUsername());
        securityContextLogoutHandler.logout(request, response, null);

        return "redirect:/";
    }

    @PostMapping("/update-profile")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateAccount(
            @RequestBody UpdateProfileRequest updateProfileRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        CustomUserDetails user = customUserDetailsService.getCurrentContextUser();
        String newName = updateProfileRequest.name();
        String newEmail = updateProfileRequest.email();

        userService.updateUser(user.getUsername(), newName, newEmail);

        user.setName(newName);
        user.setEmail(newEmail);

        return new UserResponse(user.getUsername(), newName, newEmail);
    }

    @PostMapping("/update-password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest) {
        CustomUserDetails user = customUserDetailsService.getCurrentContextUser();
        String oldPassword = updatePasswordRequest.oldPassword();
        String newPassword = updatePasswordRequest.newPassword();

        userService.changePassword(user.getUsername(), oldPassword, newPassword);
    }
}
