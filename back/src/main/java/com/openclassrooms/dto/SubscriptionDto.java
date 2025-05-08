package com.openclassrooms.dto;

import com.openclassrooms.model.Subscription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDto {
    private Long id;
    private Long userId;
    private Long themeId;
    private String themeName;
    private LocalDateTime dateSubscription;
    
    public static SubscriptionDto fromEntity(Subscription subscription) {
        SubscriptionDto dto = new SubscriptionDto();
        dto.setId(subscription.getId());
        dto.setUserId(subscription.getUser().getId());
        dto.setThemeId(subscription.getTheme().getId());
        dto.setThemeName(subscription.getTheme().getName());
        dto.setDateSubscription(subscription.getDateSubscription());
        return dto;
    }
} 