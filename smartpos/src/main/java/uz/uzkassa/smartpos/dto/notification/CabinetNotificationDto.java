package uz.uzkassa.smartpos.dto.notification;

import uz.uzkassa.smartpos.domain.postgres.Notification;
import uz.uzkassa.smartpos.enums.NotificationCategory;
import uz.uzkassa.smartpos.enums.NotificationTarget;
import uz.uzkassa.smartpos.enums.NotificationType;
import uz.uzkassa.smartpos.utils.LanguageUtils;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link Notification}
 */
public record CabinetNotificationDto(
        Long id,
        LocalDateTime createdAt,
        String titleUz,
        String titleRu,
        String titleCyrillic,
        String messageUz,
        String messageRu,
        String messageCyrillic,
        NotificationType type,
        NotificationTarget target,
        String buttonUz,
        String buttonRu,
        String buttonCyrillic,
        String buttonLink,
        NotificationCategory category,
        boolean read,
        String imageUrl
) implements Serializable {
    public String getTitle() {
        return switch (LanguageUtils.getLanguage()) {
            case uz -> titleUz;
            case cyrillic -> titleCyrillic;
            default -> titleRu;
        };
    }
}