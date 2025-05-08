package com.openclassrooms.dto;

import com.openclassrooms.model.Comment;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String content;
    private Long authorId;
    private String authorUsername;
    private Long articleId;
    private LocalDateTime createdAt;
    
    public static CommentDto fromEntity(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        if (comment.getAuthor() != null) {
            dto.setAuthorId(comment.getAuthor().getId());
            dto.setAuthorUsername(comment.getAuthor().getUsername());
        }
        if (comment.getArticle() != null) {
            dto.setArticleId(comment.getArticle().getId());
        }
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
} 