package uz.uzkassa.smartpossupply.domain.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.domain.auth.User;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;

import java.io.Serial;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "notification_read", indexes = {
        @Index(columnList = "notification_id", name = "notification_read_notification_id_idx"),
        @Index(columnList = "user_id", name = "notification_read_user_id_idx"),
})
@Entity
public class NotificationRead extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 45L;

    @NotNull
    @Column(name = "notification_id", nullable = false)
    Long notificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", updatable = false, insertable = false)
    Notification notification;

    @NotNull
    @Column(name = "user_id", nullable = false)
    Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    User user;

}
