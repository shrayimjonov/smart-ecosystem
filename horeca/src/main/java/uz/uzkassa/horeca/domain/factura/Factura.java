package uz.uzkassa.horeca.domain.factura;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import uz.uzkassa.horeca.domain.base.AbstractAuditingEntity;
import uz.uzkassa.horeca.domain.company.Company;
import uz.uzkassa.horeca.domain.stock.Stock;
import uz.uzkassa.horeca.dto.eimzo.SignatureData;
import uz.uzkassa.horeca.dto.factura.FacturaDTO;
import uz.uzkassa.horeca.dto.factura.FacturaDocDTO;
import uz.uzkassa.horeca.dto.factura.Status;
import uz.uzkassa.horeca.dto.factura.supply.FacturaLinkDTO;
import uz.uzkassa.horeca.dto.factura.supply.SupplyFacturaDTO;
import uz.uzkassa.horeca.enums.EntityTypeEnum;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(
    name = "factura",
    indexes = {
        @Index(columnList = "buyer_id", name = "factura_buyer_id_idx"),
        @Index(columnList = "seller_id", name = "factura_seller_id_idx"),
        @Index(columnList = "company_id", name = "factura_company_id_idx"),
        @Index(columnList = "factura_no", name = "factura_factura_no_idx"),
        @Index(columnList = "factura_date", name = "factura_factura_date_idx"),
        @Index(columnList = "factura_id", name = "factura_factura_id_idx"),
    }
)
@SQLDelete(sql = "UPDATE factura SET deleted = 'true' WHERE id=?")
public class Factura extends AbstractAuditingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 349583495843L;

    @Column(name = "factura_detail_id")
    Long facturaDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_detail_id", updatable = false, insertable = false)
    FacturaDetail facturaDetail;

    @Column(name = "increment_number")
    Long incrementNumber;

    @Column(name = "outgoing", columnDefinition = "boolean default false")
    boolean outgoing = false;

    @Column(name = "version", columnDefinition = "int default 1")
    Integer version = 1;

    @Column(name = "factura_type")
    Integer facturaType;

    @Column(name = "single_sided_type")
    Integer singleSidedType;

    @Column(name = "factura_id", unique = true)
    String facturaId;

    @Column(name = "factura_no")
    String facturaNo;

    @Column(name = "factura_date")
    LocalDate facturaDate;

    @Column(name = "factura_product_id")
    String facturaProductId;

    @Column(name = "buyer_id")
    Long buyerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", updatable = false, insertable = false)
    Company buyer;

    @Column(name = "seller_id")
    Long sellerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", updatable = false, insertable = false)
    Company seller;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @Column(name = "stock_id")
    Long stockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id", updatable = false, insertable = false)
    Stock stock;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "sign")
    String sign;

    @Column(name = "total", precision = 25, scale = 2)
    BigDecimal total;

    @Column(name = "total_lgota_vat_sum", precision = 25, scale = 2)
    BigDecimal totalLgotaVatSum;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    Status status = Status.DRAFT;

    @Column(name = "notes")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String notes;

    @Column(name = "send_number")
    String sendNumber;

    @Column(name = "send_by")
    String sendBy;

    @Column(name = "send_date")
    LocalDateTime sendDate;

    @Column(name = "approved_number")
    String approvedNumber;

    @Column(name = "approved_by")
    String approvedBy;

    @Column(name = "approved_date")
    LocalDateTime approvedDate;

    @Column(name = "agent_approved_number")
    String agentApprovedNumber;

    @Column(name = "agent_approved_by")
    String agentApprovedBy;

    @Column(name = "agent_approved_date")
    LocalDateTime agentApprovedDate;

    @Column(name = "error_message")
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    String errorMessage;

    @Override
    public String getName() {
        return this.getFacturaNo();
    }

    @Override
    public Long getEntityId() {
        return this.id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.FACTURA;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Factura)) {
            return false;
        }
        return id != null && id.equals(((Factura) o).id);
    }

    public FacturaLinkDTO toLink() {
        FacturaLinkDTO facturaLinkDTO = new FacturaLinkDTO();
        facturaLinkDTO.setId(getId());
        facturaLinkDTO.setFacturaId(getFacturaId());
        facturaLinkDTO.setFacturaNo(getFacturaNo());
        facturaLinkDTO.setStatus(getStatus());
        facturaLinkDTO.setStatusName(getStatus().getName());
        facturaLinkDTO.setFacturaDate(getFacturaDate());
        return facturaLinkDTO;
    }

    public SupplyFacturaDTO toDTO() {
        SupplyFacturaDTO supplyFacturaDTO = new SupplyFacturaDTO();
        supplyFacturaDTO.setId(getId());
        supplyFacturaDTO.setStatus(getStatus());
        supplyFacturaDTO.setStatusName(getStatus().getName());

        supplyFacturaDTO.setNotes(getNotes());
        supplyFacturaDTO.setTotal(getTotal());
        supplyFacturaDTO.setTotalLgotaVatSum(getTotalLgotaVatSum());
        supplyFacturaDTO.setErrorMessage(getErrorMessage());

        supplyFacturaDTO.setSendNumber(getSendNumber());
        supplyFacturaDTO.setSendBy(getSendBy());
        supplyFacturaDTO.setSendDate(getSendDate());

        supplyFacturaDTO.setAgentApprovedNumber(getAgentApprovedNumber());
        supplyFacturaDTO.setAgentApprovedBy(getAgentApprovedBy());
        supplyFacturaDTO.setAgentApprovedDate(getAgentApprovedDate());

        supplyFacturaDTO.setApprovedNumber(getApprovedNumber());
        supplyFacturaDTO.setApprovedBy(getApprovedBy());
        supplyFacturaDTO.setApprovedDate(getApprovedDate());

        if (getStockId() != null) {
            supplyFacturaDTO.setStock(getStock().toCommonDTO());
        }
        supplyFacturaDTO.setBuyerId(getBuyerId());
        supplyFacturaDTO.setSellerId(getSellerId());

        return supplyFacturaDTO;
    }

    public void fromFacturaDocDTO(FacturaDocDTO facturaDocDTO) {
        setFacturaNo(facturaDocDTO.getFacturaNo());
        setFacturaDate(facturaDocDTO.getFacturaDate());
    }

    public FacturaDocDTO toFacturaDocDTO() {
        return new FacturaDocDTO(getFacturaNo(), getFacturaDate());
    }

    public FacturaDTO toFacturaDTO() {
        if (getFacturaDetail() == null) {
            return null;
        }
        FacturaDTO facturaDTO = this.toFacturaDetailDTO();
        facturaDTO.setFacturaType(getFacturaType());
        facturaDTO.setSingleSidedType(getSingleSidedType());
        facturaDTO.setFacturaId(getFacturaId());
        facturaDTO.setSellerTin(getSeller().getTin());
        facturaDTO.setSeller(getFacturaDetail().toSellerDTO(getSeller()));
        if (getBuyer() != null) {
            facturaDTO.setBuyerTin(getBuyer().getTin());
            facturaDTO.setBuyer(getFacturaDetail().toBuyerDTO(getBuyer()));
        }
        return facturaDTO;
    }

    public void setSenderData(SignatureData signatureData) {
        setSendNumber(signatureData.getNumber());
        setSendBy(signatureData.getOwner());
        setSendDate(signatureData.getDateTime());
    }

    public void setApproverData(SignatureData signatureData) {
        setApprovedNumber(signatureData.getNumber());
        setApprovedBy(signatureData.getOwner());
        setApprovedDate(signatureData.getDateTime());
    }

    private FacturaDTO toFacturaDetailDTO() {
        FacturaDTO facturaDTO = new FacturaDTO();
        facturaDTO.setFacturaDoc(toFacturaDocDTO());
        if (StringUtils.isNotEmpty(facturaDetail.getOldFacturaId())) {
            facturaDTO.setOldFacturaDoc(facturaDetail.toOldFacturaDocDTO());
        }
        if (StringUtils.isNotEmpty(facturaDetail.getContractNo()) && facturaDetail.getContractDate() != null) {
            facturaDTO.setContractDoc(facturaDetail.toContractDTO());
        }
        if (StringUtils.isNotEmpty(facturaDetail.getAgentFacturaId()) || StringUtils.isNotEmpty(facturaDetail.getEmpowermentNo())) {
            facturaDTO.setFacturaEmpowermentDoc(facturaDetail.toEmpowermentDTO());
        }
        if (StringUtils.isNoneEmpty(facturaDetail.getItemReleasedFio(), facturaDetail.getItemReleasedPinfl())) {
            facturaDTO.setItemReleasedDoc(facturaDetail.toFacturaItemReleasedDocDTO());
        }
        if (StringUtils.isNotEmpty(facturaDetail.getForeignCountryId())) {
            facturaDTO.setForeignCompany(facturaDetail.getForeignCompanyDTO());
        }
        facturaDTO.setLotId(facturaDetail.getLotId());
        facturaDTO.setProductList(facturaDetail.toProductListDTO(facturaDetail));
        facturaDTO.setHasMarking(facturaDetail.getHasMarking());
        return facturaDTO;
    }
}
