package com.oneny.webflux.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TokenGeneratorTest {

    TokenGenerator tokenGenerator;

    @BeforeEach
    void setUp() {
        tokenGenerator = new TokenGenerator();
    }

    @Test
    void when_call_then_return_token() {
        // when
        String result = tokenGenerator.execute();

        assertLinesMatch(List.of("^[A-Z]{6}$"), List.of(result));
    }
}
