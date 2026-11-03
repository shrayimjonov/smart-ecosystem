package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.domain.cases.Cases;
import uz.uzkassa.cts.enums.ArrivalStatus;
import uz.uzkassa.cts.enums.FMReportReason;
import uz.uzkassa.cts.enums.SoliqTransferStatus;

import java.io.Serial;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "fiscal_module_report")
@Entity
@SQLRestriction(value = "deleted = 'false'")
@SQLDelete(sql = "UPDATE fiscal_module_report SET deleted = 'true' WHERE id=?")
public class FiscalModuleReport extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 8276611707L;

    @Column(name = "case_id")
    Long caseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", insertable = false, updatable = false)
    Cases cases;

    @Column(name = "customer_id")
    Long customerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", insertable = false, updatable = false)
    Customer customer;

    @Column(name = "device_serial_number")
    String deviceSerialNumber;

    @Column(name = "fm_serial_number", unique = true)
    String fmSerialNumber;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "arrival_status")
    ArrivalStatus arrivalStatus = ArrivalStatus.NOT_COME;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason")
    FMReportReason reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "soliq_transfer_status")
    SoliqTransferStatus soliqTransferStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "soliq_transfer_status")
    private SoliqTransferStatus soliqTransferStatus;

    @Column(name = "arrival_date")
    LocalDateTime arrivalDate;
}
