package com.oneny.webflux.service;

import com.oneny.webflux.common.repository.AuthEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.internal.util.MockUtil;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveSelectOperation;
import org.springframework.data.relational.core.query.CriteriaDefinition;
import org.springframework.data.relational.core.query.Query;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    R2dbcEntityTemplate mockR2dbcEntityTemplate;

    @InjectMocks
    AuthService authService;

    @Mock
    ReactiveSelectOperation.ReactiveSelect<AuthEntity> mockReactiveSelect;

    @Mock
    ReactiveSelectOperation.ReactiveSelect<AuthEntity> mockTerminationSelect;

    @Captor
    ArgumentCaptor<Query> queryArgumentCaptor;

    @Test
    void authServiceNotNull(){
        assertThat(authService).isNotNull();
        assertThat(MockUtil.isMock(mockR2dbcEntityTemplate)).isTrue();
    }

    @Nested
    class GetNameByToken {

        String token;

        @BeforeEach
        void setUp() {
            lenient().when(mockR2dbcEntityTemplate.select(AuthEntity.class))
                    .thenReturn(mockReactiveSelect);

            lenient().when(mockReactiveSelect.matching(any()))
                    .thenReturn(mockTerminationSelect);

            token = "valid_token";
        }

        @Test
        void when_auth_entity_is_empty_then_returns_empty_mono() {
            // given
            when(mockTerminationSelect.one())
                    .thenReturn(Mono.empty());

            // when
            Mono<String> result = authService.getNameByToken(token);

            // then
            StepVerifier.create(result)
                    .verifyComplete();

            // 내부 구현의 동작 방식이 검증된 것이 아니라면 아래처럼 테스트하는 것도 고민해봐야 함
            verify(mockReactiveSelect).matching(queryArgumentCaptor.capture());

            Query actualQueryOptional = queryArgumentCaptor.getValue();
            assertThat(actualQueryOptional.getCriteria()).isNotNull();
            CriteriaDefinition actualQuery = actualQueryOptional.getCriteria().get();

            assertThat(actualQuery.getColumn().getReference()).isEqualTo("token");
            assertThat(actualQuery.getValue()).isEqualTo(token);
        }

        @Test
        void when_auth_entity_is_not_empty_then_returns_name() {
            // given
            long userId = 100L;
            AuthEntity authEntity = new AuthEntity(1L, userId, token);

            when(mockTerminationSelect.one())
                    .thenReturn(Mono.just(authEntity));

            // when
            Mono<String> result = authService.getNameByToken(token);

            // then
            StepVerifier.create(result)
                    .expectNext(String.valueOf(userId))
                    .verifyComplete();
        }

        @Test
        void when_token_is_null_then_returns_mono_error() {
            // given
            token = null;

            // when
            Mono<String> result = authService.getNameByToken(token);

            // then
            StepVerifier.create(result)
                    .verifyErrorSatisfies(e -> {
                        assertThat(e)
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("token is invalid", e.getMessage());
                    });

            verify(mockR2dbcEntityTemplate, never()).select(any());
        }

        @Test
        void when_token_is_empty_then_returns_mono_error() {
            // given
            token = "";

            // when
            Mono<String> result = authService.getNameByToken(token);

            // then
            StepVerifier.create(result)
                    .verifyErrorSatisfies(e -> {
                        assertThat(e)
                                .isInstanceOf(IllegalArgumentException.class)
                                .hasMessage("token is invalid", e.getMessage());
                    });
        }

        @Test
        void when_token_is_admin_then_returns_admin() {
            // given
            token = "admin";

            // when
            Mono<String> result = authService.getNameByToken(token);

            // then
            StepVerifier.create(result)
                    .expectNext("admin")
                    .verifyComplete();
        }
    }
}
