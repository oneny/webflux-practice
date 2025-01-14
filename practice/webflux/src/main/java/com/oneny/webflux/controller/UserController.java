package com.oneny.webflux.controller;

import com.oneny.webflux.controller.dto.ProfileImageResponse;
import com.oneny.webflux.controller.dto.UserResponse;
import com.oneny.webflux.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Mono<UserResponse> getUserById(@PathVariable String userId) {
        return ReactiveSecurityContextHolder
                .getContext()
                .flatMap(context -> {
                    String name = context.getAuthentication().getName();

                    if (!name.equals(userId)) {
                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
                    }

                    return userService.findById(userId)
                            .map(user -> {
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
                            }).switchIfEmpty(
                                    Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))
                            );
                });
    }
}
