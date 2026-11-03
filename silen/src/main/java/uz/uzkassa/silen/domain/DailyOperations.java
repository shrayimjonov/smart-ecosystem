package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.enumeration.DailyOperationTypes;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "daily_operations")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DailyOperations extends BaseOrganizationEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1123452343234L;

    @Column(name = "amount_start")
    private BigDecimal amountStart;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "amount_end")
    private BigDecimal amountEnd;

    @Column(name = "product_id")
    private String productId;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    private Product product;

    @Column(name = "serial_id")
    private String serialId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serial_id", updatable = false, insertable = false)
    private SerialNumber serialNumber;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private DailyOperationTypes type;

    @Column(name = "operation_date")
    private LocalDateTime operationDate;

}
