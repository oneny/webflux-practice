package com.oneny.webflux.service;

import com.oneny.webflux.common.EmptyImage;
import com.oneny.webflux.common.Image;
import com.oneny.webflux.common.User;
import com.oneny.webflux.common.repository.AuthEntity;
import com.oneny.webflux.common.repository.UserEntity;
import com.oneny.webflux.controller.UserController;
import com.oneny.webflux.repository.ImageHttpClient;
import com.oneny.webflux.repository.UserR2dbcRepository;
import com.oneny.webflux.repository.UserReactorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final ImageHttpClient imageHttpClient;
    private final UserR2dbcRepository userRepository;
    private final R2dbcEntityTemplate entityTemplate;

    public Mono<User> findById(String userId) {
        return userRepository.findById(Long.parseLong(userId))
                .flatMap(userEntity -> {
                    String imageId = userEntity.getProfileImageId();

                    return imageHttpClient.getImageResponseByImageId(imageId)
                            .map(imageResp -> new Image(
                                    imageResp.getId(),
                                    imageResp.getName(),
                                    imageResp.getUrl()
                            ))
                            .switchIfEmpty(Mono.just(new EmptyImage()))
                            .map(image -> {
                                Optional<Image> profileImage = Optional.empty();
                                if (!(image instanceof EmptyImage)) {
                                    profileImage = Optional.of(image);
                                }
                                return map(userEntity, profileImage);
                            });
                });
    }

    @Transactional
    public Mono<User> createUser(String name, Integer age, String password, String profileImageId) {
        return userRepository.save(new UserEntity(
                        name,
                        age,
                        profileImageId,
                        password))
                .flatMap(userEntity -> {
                    String token = new TokenGenerator().execute();
                    AuthEntity auth = new AuthEntity(userEntity.getId(), token);

                    return entityTemplate.insert(auth)
                            .map(authEntity -> userEntity);
                })
                .map(userEntity -> map(userEntity, Optional.of(new EmptyImage())));
    }

    private User map(UserEntity userEntity, Optional<Image> profileImage) {
        return new User(
                userEntity.getId().toString(),
                userEntity.getName(),
                userEntity.getAge(),
                profileImage,
                List.of(),
                0L
        );
    }
}
