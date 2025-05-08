package com.openclassrooms.dto;

import lombok.Data;

@Data
public class ArticleCreateDto {
    private String title;
    private String content;
    private String status;
    private Long themeId;
} 