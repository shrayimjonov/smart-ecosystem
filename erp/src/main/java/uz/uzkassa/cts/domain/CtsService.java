package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.cts.converter.OriginConverter;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.Origin;
import uz.uzkassa.cts.enums.ServiceRegistrationType;
import uz.uzkassa.cts.enums.ServiceType;
import uz.uzkassa.cts.enums.SupplierCategory;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cts_service")
@Getter
@Setter
@SQLDelete(sql = "UPDATE cts_service SET deleted = 'true' WHERE id=?")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CtsService extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 1891126496L;

    @NotNull
    @Column(name = "name")
    String name;

    @Column(name = "code", length = 11)
    String code;

    @NotNull
    @Column(name = "price")
    Double price;

    @Column(name = "with_quantity")
    Boolean withQuantity;

    @Column(name = "for_xfile")
    Boolean forXfile;

    @Column(name = "bonus", precision = 15, scale = 2)
    BigDecimal bonus = BigDecimal.ZERO;

    @Column(name = "catalog_code")
    String catalogCode;

    @Column(name = "catalog_name")
    String catalogName;

    @Column(name = "package_name")
    String packageName;

    @Column(name = "package_code")
    String packageCode;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    SupplierCategory category;

    @NotNull
    @Enumerated(EnumType.STRING)
    ServiceRegistrationType registrationType;

    @Column(name = "service_type")
    @Enumerated(EnumType.STRING)
    ServiceType serviceType;

    @ManyToMany(mappedBy = "ctsServices", cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    List<Tariff> tariffs = new ArrayList<>();

    @Convert(converter = OriginConverter.class)
    @Column(name = "Origin", columnDefinition = "int default 3")
    Origin origin;

    @Column(name = "bonus_percentage")
    BigDecimal bonusPercentage;

    @Column(name = "billing_id", unique = true)
    Long billingId;
}
