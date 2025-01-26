package com.oneny.webflux.runner;

import com.oneny.webflux.common.repository.UserEntity;
import com.oneny.webflux.repository.UserR2dbcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
//@Component
@RequiredArgsConstructor
public class PersonInsertRunner implements CommandLineRunner {

    private final UserR2dbcRepository userR2dbcRepository;

    @Override
    public void run(String... args) throws Exception {
        UserEntity savedUser = userR2dbcRepository.save(new UserEntity("oneny", 30, "1", "1q2w3e4r!"))
                .block();

        log.info("savedUser: {}", savedUser);
    }
}
