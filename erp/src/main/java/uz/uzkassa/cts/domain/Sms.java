package uz.uzkassa.cts.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.enums.ProviderType;
import uz.uzkassa.cts.enums.SmsStatus;

import java.io.Serial;

@Entity
@Table(name = "sms")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Sms extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 4855524841L;

    @Column(name = "recipient")
    String recipient;

    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    @Column(name = "message")
    String message;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    SmsStatus status = SmsStatus.PENDING;

    @Column(name = "provider_type")
    @Enumerated(EnumType.STRING)
    ProviderType providerType = ProviderType.ETC;

}
