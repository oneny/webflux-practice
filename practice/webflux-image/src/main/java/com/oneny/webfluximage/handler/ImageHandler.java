package com.oneny.webfluximage.handler;

import com.oneny.webfluximage.handler.dto.ImageResponse;
import com.oneny.webfluximage.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ImageHandler {

    private final ImageService imageService;

    public Mono<ServerResponse> getImageById(ServerRequest serverRequest) {
        String imageId = serverRequest.pathVariable("imageId");

        // ImageReactorRepository에서
        // imageId에 해당하는 이미지가 없는 경우 에러 객체 throw out하기 때문에
        // 해당 레벨에서 에러를 ResponseStatusException로 변환
        return imageService.getImageById(imageId)
                .map(image -> new ImageResponse(
                        image.getId(),
                        image.getName(),
                        image.getUrl()))
                .flatMap(imageResp -> ServerResponse.ok().bodyValue(imageResp))
                .onErrorMap(e -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
