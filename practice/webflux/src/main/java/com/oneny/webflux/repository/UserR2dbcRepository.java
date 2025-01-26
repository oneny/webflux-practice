package com.oneny.webflux.repository;

import com.oneny.webflux.common.repository.UserEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface UserR2dbcRepository extends R2dbcRepository<UserEntity, Long> {
}
