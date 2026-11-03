package uz.uzkassa.horeca.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.horeca.domain.base.SimpleEntity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "sms_history")
@Entity
public class SmsHistory extends SimpleEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 50L;

    @Column(name = "phone")
    String phone;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "message")
    String message;

    @Column(name = "code")
    String code;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "response_message")
    String responseMessage;

    @Column(name = "created_date")
    LocalDateTime createdDate = LocalDateTime.now();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SmsHistory)) {
            return false;
        }
        return id != null && id.equals(((SmsHistory) o).id);
    }
}
