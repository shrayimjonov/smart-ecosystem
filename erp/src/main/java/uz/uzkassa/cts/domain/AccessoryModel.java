package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.CommonStatus;

import java.io.Serial;

@Getter
@Setter
@Table(name = "accessory_model")
@Entity
@SQLDelete(sql = "UPDATE accessory_model SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccessoryModel extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 1976266906L;

    @Column(name = "name")
    String name;

    @Column(name = "description")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String description;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    CommonStatus status = CommonStatus.ACTIVE;
}
