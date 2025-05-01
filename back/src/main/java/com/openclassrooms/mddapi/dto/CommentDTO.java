package com.openclassrooms.mddapi.dto;

import lombok.Data;
import java.util.Date;

@Data
public class CommentDTO {
    private Long id;
    private String content;
    private String authorUsername;
    private Long authorId;
    private Long articleId;
    private Date createdAt;
} 