package com.openclassrooms.mddapi.dto;

import lombok.Data;
import java.util.Date;

@Data
public class ArticleDTO {
    private Long id;
    private String title;
    private String content;
    private String theme;
    private String authorUsername;
    private Long authorId;
    private Date createdAt;
    private int commentCount;
} 