package com.oneny.webflux.controller;

import com.oneny.webflux.common.User;
import com.oneny.webflux.controller.dto.ProfileImageResponse;
import com.oneny.webflux.controller.dto.SignupUserRequest;
import com.oneny.webflux.controller.dto.UserResponse;
import com.oneny.webflux.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("{userId}")
    public Mono<UserResponse> getUserById(@PathVariable String userId) {
        return ReactiveSecurityContextHolder
                .getContext()
                .flatMap(context -> {
                    String name = context.getAuthentication().getName();

                    if (!name.equals(userId)) {
                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
                    }

                    // userId에 해당하는 유저가 없는 경우 switchIfEmpty 실행
                    return userService.findById(userId)
                            .map(this::map)
                            .switchIfEmpty(
                                    Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))
                            );
                });
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/signup")
    public Mono<UserResponse> signupUser(@RequestBody SignupUserRequest request) {
        return userService.createUser(request.getName(), request.getAge(), request.getPassword(), request.getProfileImageId())
                .map(this::map);
    }

    private UserResponse map(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getAge(),
                user.getFollowCount(),
                user.getProfileImage().map(image ->
                        new ProfileImageResponse(
                                image.getId(),
                                image.getName(),
                                image.getUrl()
                        ))
        );
    }
}
