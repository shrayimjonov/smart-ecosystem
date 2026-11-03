package uz.uzkassa.smartpossupply.domain.company;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.smartpossupply.domain.base.SimpleEntity;
import uz.uzkassa.smartpossupply.enums.DeliveryType;

import java.io.Serial;
import java.util.Set;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 28.08.2023
 */

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
    name = "company_delivery_settings",
    indexes = {
        @Index(columnList = "company_id", name = "company_delivery_settings_company_id_idx")
    }
)
public class CompanyDeliverySettings extends SimpleEntity {

    @Serial
    private static final long serialVersionUID = 954646L;

    @NotNull
    @Column(name = "company_id", nullable = false)
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @NotNull
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    DeliveryType type;

    @Column(name = "from_day")
    Integer fromDay;

    @Column(name = "to_day")
    Integer toDay;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "warehouse_ids", columnDefinition = "jsonb")
    Set<Long> warehouseIds;

    @Column(name = "select_all", columnDefinition = "boolean default false")
    boolean selectAll = false;

}
