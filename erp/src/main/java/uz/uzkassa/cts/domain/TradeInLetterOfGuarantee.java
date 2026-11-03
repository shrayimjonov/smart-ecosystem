package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ContractStatus;

import java.io.Serial;

@Entity
@Table(name = "trade_in_letter_of_guarantee")
@Getter
@Setter
@SQLDelete(sql = "UPDATE tariff SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TradeInLetterOfGuarantee extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 2489575865L;

    @NotNull
    @Column(name = "customer_id")
    private Long customerId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    private Customer customer;

    @Column(name = "trade_in_device_id")
    private Long tradeInDeviceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trade_in_device_id", insertable = false, updatable = false)
    private TradeInDevice tradeInDevice;

    @Column(name = "device_serial_number", unique = true)
    private String deviceSerialNumber;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    @Column(name = "file_uri")
    private String fileUri;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "trade_in_contract_id")
    private Long tradeInContractId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "trade_in_contract_id", insertable = false, updatable = false)
    private TradeInContract tradeInContract;
}
