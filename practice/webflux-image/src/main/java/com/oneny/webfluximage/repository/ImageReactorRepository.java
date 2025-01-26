package com.oneny.webfluximage.repository;

import com.oneny.webfluximage.entity.common.repository.ImageEntity;
import com.oneny.webfluximage.entity.common.repository.UserEntity;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

@Slf4j
public class ImageReactorRepository {

    private final Map<String, ImageEntity> imageMap;

    public ImageReactorRepository() {
        imageMap = Map.of(
                "1", new ImageEntity("1", "profileImage", "https://dailyone.com/images/1"),
                "2", new ImageEntity("2", "peter's image", "https://dailyone.com/images/2")
        );
    }

    public Mono<ImageEntity> findById(String id) {
        return Mono.create(sink -> {
            log.info("ImageRepository.findById: {}", id);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            ImageEntity image = imageMap.get(id);
            // 이미지가 없는 경우 error throw out
            if (image == null) {
                sink.error(new RuntimeException("image not found"));
            } else {
                sink.success(image);
            }
        });
    }

    public Mono<ImageEntity> findWithContext() {
        return Mono.deferContextual(context -> {
            Optional<UserEntity> userOptional = context.getOrEmpty("user");
            if (userOptional.isEmpty()) throw new RuntimeException("user not found");

            return Mono.just(userOptional.get().getProfileImageId());
        }).flatMap(this::findById);
    }
}
