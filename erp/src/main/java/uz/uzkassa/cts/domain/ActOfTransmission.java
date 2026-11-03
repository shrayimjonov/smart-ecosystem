package uz.uzkassa.cts.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.dto.actoftransmission.ActOfTransmissionShortDTO;
import uz.uzkassa.cts.enums.ActDocFlowStatus;
import uz.uzkassa.cts.enums.CompanyType;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.PrefixEnum;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Table(name = "act_of_transmission")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE act_of_transmission SET deleted = 'true' WHERE id=?")
@SQLRestriction(value = "deleted = 'false'")
public class ActOfTransmission extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 6109294453L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id")
    ContractForLeasing contract;

    @Column(name = "total_product_count")
    Integer totalProductCount;

    @Column(name = "total_price_with_vat")
    BigDecimal totalPriceWithVat;

    @Column(name = "total_price")
    BigDecimal totalPrice;

    @Column(name = "total_vat_price")
    BigDecimal totalVatPrice;

    @Column(name = "price_for_1_pc")
    BigDecimal priceFor1pc;

    @Column(name = "period")
    Integer period;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    ContractStatus status = ContractStatus.CREATED;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_flow_status")
    ActDocFlowStatus docFlowStatus;

    @Column(name = "accept_date")
    LocalDateTime acceptDate;

    @Column(name = "doc_flow_reason")
    String docFlowReason;

    @Column(name = "file_uri")
    String fileUri;

    @Column(name = "file_name")
    String fileName;

    @Column(name = "content_type")
    String contentType;

    @Column(name = "schedule_file_uri")
    String scheduleFileUri;

    @Column(name = "schedule_file_name")
    String scheduleFileName;

    @Column(name = "schedule_content_type")
    String scheduleContentType;

    @Column(name = "power_attorney_file_uri")
    String powerAttorneyFileUri;

    @Column(name = "power_attorney_file_name")
    String powerAttorneyFileName;

    @Column(name = "power_attorney_content_type")
    String powerAttorneyContentType;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    ActOfTransmissionInvoice invoice;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_type")
    CompanyType companyType;

    @Column(name = "paid_date")
    LocalDate paidDate;

    @OneToMany(mappedBy = "actOfTransmission", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<ActOfTransmissionDetail> details = new HashSet<>();

    public ActOfTransmissionShortDTO toActOfTransmissionShortDTO() {
        return new ActOfTransmissionShortDTO(this.getId(), this.getNumber(), this.getCreatedDate());
    }

    public void addDetail(ActOfTransmissionDetail detail) {
        detail.setActOfTransmission(this);
        details.add(detail);
    }

    public String getNumber() {
        return PrefixEnum.CLA + "/" + this.getId();
    }


    public String getDeviceModelName() {
        return Collections.min(details, Comparator.comparing(ActOfTransmissionDetail::getId)).getDevice().getModel().getName();
    }

    public String getDeviceSerialNumbers() {
        StringBuilder builder = new StringBuilder();
        for (ActOfTransmissionDetail detail : details) {
            builder.append(detail.getDevice().getSerialNumber());
            builder.append(", ");
        }
        String result = builder.toString();
        if (StringUtils.isNotEmpty(result))
            return result.substring(0, result.lastIndexOf(","));
        return "";
    }

    public String getModelName() {
        if (!details.isEmpty()) {
            ActOfTransmissionDetail detail = details.iterator().next();
            String modelName = detail.getDevice().getModel().getName();
            modelName = modelName.replace("машина", "машина(ы)");
            return modelName;
        }
        return "";
    }
}
