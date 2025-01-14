package com.oneny.webflux.controller.dto;

import lombok.Data;

import java.util.Optional;

public record UserResponse(
        String id,
        String name,
        int age,
        Long followCount,
        Optional<ProfileImageResponse> image
) {
}
