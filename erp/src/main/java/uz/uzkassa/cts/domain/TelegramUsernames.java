package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.domain.base.SimpleEntity;

import java.io.Serial;
import java.io.Serializable;


@Getter
@Setter
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "telegram_usernames",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"employee_id", "username"})})
public class TelegramUsernames extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 6772467469L;

    @Column(name = "employee_id", nullable = false, unique = true)
    Long employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", updatable = false, insertable = false)
    User employee;

    @Column(name = "username", nullable = false, unique = true)
    String username;
}
