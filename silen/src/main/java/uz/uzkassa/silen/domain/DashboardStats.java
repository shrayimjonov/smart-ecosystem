package uz.uzkassa.silen.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.enumeration.DailyOperationTypes;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@Entity
@NoArgsConstructor
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "dashboard_stats",
        indexes = {
                @Index(columnList = "organization_id,type", name = "dashboard_stats_org_sta_idx")
        })
@SQLDelete(sql = "UPDATE dashboard_stats SET deleted = 'true' WHERE id=?")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@DynamicUpdate
public class DashboardStats extends BaseOrganizationEntity implements Serializable {

    @Column(name = "product_id")
    private String productId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    private Product product;

    private BigDecimal quantity;

    @Column(name = "unit", columnDefinition = "int4 default 0", nullable = false)
    private int unit;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private DailyOperationTypes type;

    @Column(name = "operation_date")
    private LocalDateTime operationDate;
}
