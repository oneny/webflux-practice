package com.oneny.webflux.repository;

import com.oneny.webflux.service.ImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ImageHttpClient {

    private final WebClient imageWebClient;

    public Mono<ImageResponse> getImageResponseByImageId(String imageId) {
        Map<String, String> uriVariableMap = Map.of("imageId", imageId);

        return imageWebClient.get()
                .uri("/api/images/{imageId}", uriVariableMap)
                .retrieve()
                .toEntity(ImageResponse.class)
                .map(HttpEntity::getBody);
    }
}
