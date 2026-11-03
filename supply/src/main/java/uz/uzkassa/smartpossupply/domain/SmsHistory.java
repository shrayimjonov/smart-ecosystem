package uz.uzkassa.smartpossupply.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.base.BaseEntity;
import uz.uzkassa.smartpossupply.enums.SmsStatus;

import java.io.Serial;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "sms_history")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SmsHistory extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 15L;

    @Column(name = "phone")
    String phone;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "message")
    String message;

    @Column(name = "code")
    String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    SmsStatus status;

    @Column(name = "created_date")
    LocalDateTime createdDate = LocalDateTime.now();
}
