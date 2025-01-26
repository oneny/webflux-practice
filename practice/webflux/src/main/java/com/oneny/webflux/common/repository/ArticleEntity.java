package com.oneny.webflux.common.repository;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Table;

@Data
public class ArticleEntity {
    private final String id;
    private final String title;
    private final String content;
    private final String userId;
}
