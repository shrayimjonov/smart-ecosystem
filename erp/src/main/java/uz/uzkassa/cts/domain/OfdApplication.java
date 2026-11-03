package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ApplicationType;
import uz.uzkassa.cts.enums.OfdStatus;
import uz.uzkassa.cts.enums.Reason;

import java.io.Serial;

@Entity
@Table(name = "ofd_application",
        indexes = {
                @Index(columnList = "ofd_id", name = "ofd_application_ofd_id_idx"),
                @Index(columnList = "ofd_status", name = "ofd_application_ofd_status_idx"),
                @Index(columnList = "application_type", name = "ofd_application_application_type_idx"),
                @Index(columnList = "customer_id", name = "ofd_application_customer_id_idx"),
                @Index(columnList = "customer_branch_id", name = "ofd_application_customer_branch_id_idx"),
                @Index(columnList = "device_id", name = "ofd_application_device_id_idx"),
                @Index(columnList = "fiscal_module_id", name = "ofd_application_fiscal_module_id_idx")
        })
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@SQLDelete(sql = "UPDATE ofd_application SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class OfdApplication extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 376675836L;

    @Column(name = "ofd_id")
    Long ofdId;

    @Column(name = "office_id")
    Long officeId;

    @Column(name = "office_file_id")
    Long officeFileId;

    @Column(name = "contract_file_id")
    Long contractFileId;

    @Column(name = "application_file_id")
    Long applicationFileId;

    @Column(name = "application_file_url")
    String applicationFileUrl;

    @Column(name = "ofd_status")
    @Enumerated(EnumType.STRING)
    OfdStatus ofdStatus;

    public void setOfdStatus(OfdStatus ofdStatus) {
        if (ofdStatus != null) {
            this.ofdStatus = ofdStatus;
        }
    }

    @Column(name = "application_type")
    @Enumerated(EnumType.STRING)
    ApplicationType applicationType;

    @Column(name = "customer_id")
    Long customerId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", updatable = false, insertable = false)
    Customer customer;

    @Column(name = "customer_branch_id")
    Long customerBranchId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_branch_id", updatable = false, insertable = false)
    CustomerBranch customerBranch;

    @Column(name = "device_id")
    Long deviceId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "device_id", updatable = false, insertable = false)
    Device device;

    @Column(name = "fiscal_module_id")
    Long fiscalModuleId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fiscal_module_id", updatable = false, insertable = false)
    FiscalModule fiscalModule;

    @Column(name = "reason", length = 1000)
    String reason;

    @Column(name = "swap_reason")
    @Enumerated(EnumType.STRING)
    Reason swapReason;

    @Column(name = "reason_text", length = 1000)
    String swapReasonText;
}