package com.oneny.webflux.common.repository;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("USER")
public class UserEntity {

    @Id
    private final Long id;
    private final String name;
    private final int age;
    private final String profileImageId;
    private final String password;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @PersistenceCreator
    public UserEntity(Long id, String name, int age, String profileImageId, String password) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.profileImageId = profileImageId;
        this.password = password;
    }

    public UserEntity(String name, int age, String profileImageId, String password) {
        this(null, name, age, profileImageId, password);
    }
}
