package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.converter.OriginConverter;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.AccessoryType;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.Origin;

import java.io.Serial;
import java.math.BigDecimal;

@Getter
@Setter
@Table(name = "accessory")
@Entity
@SQLDelete(sql = "UPDATE accessory SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class Accessory extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 2010015480L;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "model_id")
    private Long modelId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", updatable = false, insertable = false)
    AccessoryModel model;

    @Enumerated(EnumType.STRING)
    @Column(name = "accessory_type")
    private AccessoryType accessoryType;

    @Column(name = "description")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private String description;

    @Column(name = "price", precision = 25, scale = 2)
    private BigDecimal price;

    @Column(name = "rent_price")
    private BigDecimal rentPrice;

    @Column(name = "catalog_code", nullable = false)
    private String catalogCode;

    @Column(name = "catalog_name", nullable = false)
    private String catalogName;

    @Column(name = "package_name", nullable = false)
    private String packageName;

    @Column(name = "package_code", nullable = false)
    private String packageCode;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "code_for_ui")
    private String codeForUI;

    @Column(name = "bonus")
    private BigDecimal bonus;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CommonStatus status = CommonStatus.ACTIVE;

    @Convert(converter = OriginConverter.class)
    @Column(name = "Origin", columnDefinition = "int default 3")
    private Origin origin;
}
