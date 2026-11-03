package uz.uzkassa.cts.domain.report;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.cts.domain.address.Region;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.*;
import uz.uzkassa.cts.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "contract_report")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContractReport extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 3134538964L;

    @Column(name = "serial_number", unique = true, nullable = false)
    String serialNumber;

    @Column(name = "tin")
    String tin;

    @Column(name = "name")
    String name;

    @Column(name = "realisation")
    @Enumerated(EnumType.STRING)
    ContractReportPaymentType realisation;

    @Column(name = "contract_number")
    String contractNumber;

    @Column(name = "invoice_status")
    @Enumerated(EnumType.STRING)
    ContractReportInvoiceStatus invoiceStatus;

    @Column(name = "amount")
    BigDecimal amount;

    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    @Column(name = "comment")
    String comment;

    @Column(name = "document_status")
    @Enumerated(EnumType.STRING)
    ActDocFlowStatus documentStatus;

    @Column(name = "sale_status", length = 50)
    @Enumerated(EnumType.STRING)
    ContractReportInvoiceSaleStatus saleStatus = ContractReportInvoiceSaleStatus.EMPTY;

    @Column(name = "device_replacement")
    String deviceReplacement;

    @Column(name = "reason")
    String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "check_status")
    CheckStatus checkStatus;

    @Column(name = "date")
    LocalDateTime date;

    @Column(name = "region_id")
    Long regionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", updatable = false, insertable = false)
    Region region;

    public void setSerialNumber(String serialNumber) {
        addChangeLog("serialNumber", this.serialNumber, serialNumber);
        this.serialNumber = serialNumber;
    }

    public void setTin(String tin) {
        addChangeLog("tin", this.tin, tin);
        this.tin = tin;
    }

    public void setName(String name) {
        addChangeLog("name", this.name, name);
        this.name = name;
    }

    public void setRealisation(ContractReportPaymentType realisation) {
        addChangeLog("realisation", this.realisation != null ? this.realisation.getName() : null, realisation != null ? realisation.getName() : null);
        this.realisation = realisation;
    }

    public void setContractNumber(String contractNumber) {
        addChangeLog("contractNumber", this.contractNumber, contractNumber);
        this.contractNumber = contractNumber;
    }

    public void setInvoiceStatus(ContractReportInvoiceStatus invoiceStatus) {
        addChangeLog("invoiceStatus", this.invoiceStatus != null ? this.invoiceStatus.getName() : null, invoiceStatus != null ? invoiceStatus.getName() : null);
        this.invoiceStatus = invoiceStatus;
    }

    public void setAmount(BigDecimal amount) {
        addChangeLog("amount", this.amount != null ? String.valueOf(this.amount) : null, amount != null ? String.valueOf(amount) : null);
        this.amount = amount;
    }

    public void setSaleStatus(ContractReportInvoiceSaleStatus saleStatus) {
        addChangeLog("saleStatus", this.saleStatus != null ? this.saleStatus.getName() : null, saleStatus != null ? saleStatus.getName() : null);
        this.saleStatus = saleStatus;
    }

    public void setRegion(Region region) {
        addChangeLog("region", this.region != null ? this.region.getName() : null, region != null ? region.getName() : null);
        this.region = region;
    }

    public void setDocumentStatus(ActDocFlowStatus documentStatus) {
        addChangeLog("documentStatus", this.documentStatus != null ? this.documentStatus.getName() : null, documentStatus != null ? documentStatus.getName() : null);
        this.documentStatus = documentStatus;
    }

    public void setDeviceReplacement(String deviceReplacement) {
        addChangeLog("deviceReplacement", this.deviceReplacement, deviceReplacement);
        this.deviceReplacement = deviceReplacement;
    }

    public void setDate(LocalDateTime date) {
        addChangeLog("date", this.date != null ? String.valueOf(this.date) : null, date != null ? String.valueOf(date) : null);
        this.date = date;
    }

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.CONTRACT_REPORT;
    }

    @Override
    public String getEntityName() {
        return this.name;
    }
}
