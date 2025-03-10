package com.positivewand.tourin.web.user;

import com.positivewand.tourin.domain.user.dto.UserDto;
import com.positivewand.tourin.domain.user.UserService;
import com.positivewand.tourin.web.aop.PaginationAspect.PaginationHeader;
import com.positivewand.tourin.web.user.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    @PaginationHeader
    public Page<UserResponse> getUsers(
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize
    ) {
        Page<UserDto> userDtoPage = null;

        if(query == null) {
            userDtoPage = userService.findUsers(pageNo, pageSize);
        } else {
            userDtoPage = userService.findUsers(query, pageNo, pageSize);
        }

        return userDtoPage.map((UserDto userDto) -> new UserResponse(userDto.username(), userDto.name()));
    }

    @GetMapping("/users/{userId}")
    public UserResponse getUser(@PathVariable(name = "userId") String userId) {
        UserDto user = userService.findUser(userId);

        return new UserResponse(user.username(), user.name());
    }
}
