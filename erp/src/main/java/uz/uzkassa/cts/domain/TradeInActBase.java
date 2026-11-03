package uz.uzkassa.cts.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.domain.base.AbstractAuditingEntity;
import uz.uzkassa.cts.enums.ActDocFlowStatus;
import uz.uzkassa.cts.enums.ContractStatus;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class TradeInActBase extends AbstractAuditingEntity {

    @Serial
    private static final long serialVersionUID = 4803031676L;

    @Column(name = "total_product_count")
    private Integer totalProductCount;

    @Column(name = "total_price_with_vat")
    private BigDecimal totalPriceWithVat;

    @Column(name = "price_for_1_pc")
    private BigDecimal priceFor1pc;

    @Column(name = "price_for_1_pc_with_vat")
    private BigDecimal priceFor1pcWithVat;

    @Column(name = "vat_1pc")
    private BigDecimal vat1pc;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ContractStatus status = ContractStatus.CREATED;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_flow_status")
    private ActDocFlowStatus docFlowStatus;

    @Column(name = "accept_date")
    private LocalDateTime acceptDate;

    @Column(name = "doc_flow_reason")
    private String docFlowReason;

    @Column(name = "file_uri")
    private String fileUri;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "power_attorney_file_uri")
    private String powerAttorneyFileUri;

    @Column(name = "power_attorney_file_name")
    private String powerAttorneyFileName;

    @Column(name = "power_attorney_content_type")
    private String powerAttorneyContentType;

    @Column(name = "image_file_uri")
    private String imageFileUri;

    @Column(name = "image_file_name")
    private String imageFileName;

    @Column(name = "image_content_type")
    private String imageContentType;

    abstract String getNumber();
}
