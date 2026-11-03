package uz.uzkassa.smartpos.dto.notification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import uz.uzkassa.smartpos.domain.postgres.Notification;
import uz.uzkassa.smartpos.enums.BillingType;
import uz.uzkassa.smartpos.enums.NotificationCategory;
import uz.uzkassa.smartpos.enums.NotificationTarget;
import uz.uzkassa.smartpos.enums.NotificationType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link Notification}
 */
public record NotificationCreateDto(
        @NotBlank String titleUz,
        @NotBlank String titleRu,
        @NotBlank String titleCyrillic,
        @NotBlank String messageUz,
        @NotBlank String messageRu,
        @NotBlank String messageCyrillic,
        @NotNull NotificationType type,
        BillingType billingType,
        @NotNull NotificationCategory category,
        @NotNull NotificationTarget target,
        String buttonUz,
        String buttonRu,
        String buttonCyrillic,
        String buttonLink,
        @NotNull LocalDateTime dateOfPublication,
        Long companyId,
        Long imageId
) implements Serializable {
}