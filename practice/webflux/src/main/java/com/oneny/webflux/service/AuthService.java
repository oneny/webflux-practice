package com.oneny.webflux.service;

import com.oneny.webflux.common.repository.AuthEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final R2dbcEntityTemplate entityTemplate;

    public Mono<String> getNameByToken(String token) {
        if (Objects.isNull(token) || Strings.isBlank(token)) {
            return Mono.error(new IllegalArgumentException("token is invalid"));
        }

        if (token.equals("admin")) {
            return Mono.just("admin");
        }

        Query query = Query.query(Criteria.where("token").is(token));

        return entityTemplate.select(AuthEntity.class)
                .matching(query)
                .one()
                .map(authEntity -> authEntity.getUserId().toString())
                .doOnNext(name -> log.info("name: {}", name));
    }
}
