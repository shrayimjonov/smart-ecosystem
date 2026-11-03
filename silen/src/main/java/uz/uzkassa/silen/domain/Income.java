package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.enumeration.IncomeStatus;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * An Income.
 */
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "income", uniqueConstraints = {@UniqueConstraint(columnNames = {"organization_id", "number"})})
@SQLDelete(sql = "UPDATE income SET deleted = true WHERE id = ?")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Income extends BaseIdentityEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "organization_id")
    private String organizationId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", updatable = false, insertable = false)
    private Organization organization;

    @Column(name = "number")
    private String number;

    @Column(name = "int_number", columnDefinition = "int4 default 0", nullable = false)
    private int intNumber;

    @Enumerated(EnumType.STRING)
    private IncomeStatus status = IncomeStatus.NEW;

    @Column(name = "store_id")
    private Long storeId;
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", updatable = false, insertable = false)
    private Store store;

    @Column(name = "description", length = 1024)
    private String description;

    @OneToMany(mappedBy = "income")
    private Set<IncomeItem> items;
}
