package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.SimpleEntity;

import java.io.Serial;

@Entity
@Table(name = "notification_read",
        indexes = {
                @Index(columnList = "notification_id", name = "notification_read_not_id_idx"),
                @Index(columnList = "user_id", name = "notification_read_user_id_idx"),
                @Index(columnList = "notification_id, user_id", name = "notification_read_not_id_user_id_idx")
        })
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationRead extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 5555771463L;

    @NotNull
    @Column(name = "notification_id", nullable = false)
    Long notificationId;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "notification_id", updatable = false, insertable = false)
    Notification notification;

    @NotNull
    @Column(name = "user_id", nullable = false)
    Long userId;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id", updatable = false, insertable = false)
    User user;
}
