package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.partyaggregation.PartyAggregationDTO;
import uz.uzkassa.silen.enumeration.PackageType;
import uz.uzkassa.silen.enumeration.PartyAggregationStatus;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * A BatchAggregation.
 */
@ToString
@Getter
@Setter
@Entity
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "party_aggregation")
@SQLDelete(sql = "UPDATE party_aggregation SET deleted = 'true' WHERE id=?")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PartyAggregation extends BaseOrganizationEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "number")
    private String number;

    @Column(name = "int_number", columnDefinition = "int4 default 0", nullable = false)
    private int intNumber;

    @Column(columnDefinition = "int4 default 0", nullable = false)
    private int unit;

    @Column(name = "gcp", length = 15)
    private String gcp;

    private Integer capacity;

    private Integer quantity;

    private String description;

    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "party_aggregation_parties", joinColumns = @JoinColumn(name = "party_aggreagtion_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)))
    private Set<String> parties = new HashSet<>();

    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "party_block_parties", joinColumns = @JoinColumn(name = "party_block_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)))
    private Set<String> blockParties = new HashSet<>();

    @Column(name = "product_id")
    private String productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", updatable = false, insertable = false)
    private Product product;

    @Column(name = "serial_id")
    private String serialId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serial_id", updatable = false, insertable = false)
    private SerialNumber serial;

    @Column(name = "store_id")
    private String storeId;

    @Column(name = "order_id")
    private String orderId;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "block_order_id")
    private String blockOrderId;

    @Column(name = "block_order_number")
    private String blockOrderNumber;

    @Column(name = "production_date")
    LocalDate productionDate = LocalDate.now();

    @Column(name = "expiration_date")
    LocalDate expirationDate;

    @Enumerated(EnumType.STRING)
    private PartyAggregationStatus status = PartyAggregationStatus.NEW;

    @Column(name = "offline", columnDefinition = "boolean default false")
    boolean isOffline;

    @Column(name = "error_message",columnDefinition = "text")
    String errorMessage;

    @Column(name = "fully", columnDefinition = "boolean default false")
    boolean fully;

    public PartyAggregationDTO toDto() {
        PartyAggregationDTO dto = setAuditValues(new PartyAggregationDTO());
        dto.setId(getId());
        dto.setNumber(getNumber());
        dto.setQuantity(getQuantity());
        dto.setCapacity(getCapacity());
        dto.setDescription(getDescription());
        dto.setGcp(getGcp());
        Hibernate.initialize(getParties());
        dto.setParties(getParties());
        Hibernate.initialize(getBlockParties());
        dto.setBlockParties(getBlockParties());
        dto.setProductId(getProductId());
        dto.setExpirationDate(getExpirationDate());
        dto.setProductionDate(getProductionDate());
        dto.setSerialId(getSerialId());
        dto.setStoreId(getStoreId());
        dto.setOffline(isOffline());
        dto.setStatus(getStatus());
        dto.setOrderId(getOrderId());
        dto.setOrderNumber(getOrderNumber());
        dto.setErrorMessage(getErrorMessage());
        dto.setUnit(PackageType.fromCode(getUnit()));
        dto.setBlockOrderId(getBlockOrderId());
        dto.setBlockOrderNumber(getBlockOrderNumber());
        dto.setFully(isFully());
        if (getSerial() != null) {
            dto.setSerial(getSerial().toSelectItem());
        }
        if (getProduct() != null) {
            dto.setProduct(getProduct().toCommonDTO());
        }
        if (getStatus() != null) {
            dto.setStatusName(getStatus().getText());
        }
        return dto;
    }

    @Override
    public SelectItem toSelectItem() {
        return new SelectItem(this.id, this.number);
    }
}
