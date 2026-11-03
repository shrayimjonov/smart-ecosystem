package uz.uzkassa.smartpossupply.domain.waybill;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.domain.acceptancetransferact.AcceptanceTransferAct;
import uz.uzkassa.smartpossupply.domain.base.AbstractAuditingEntity;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.customerbranch.CustomerBranch;
import uz.uzkassa.smartpossupply.domain.factura.Factura;
import uz.uzkassa.smartpossupply.domain.stocktransfer.StockTransfer;
import uz.uzkassa.smartpossupply.dto.waybill.OldWaybillDocDTO;
import uz.uzkassa.smartpossupply.dto.waybill.SupplyWaybillDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillClientDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillCompanyDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillContractDocDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillDocDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillLinkDTO;
import uz.uzkassa.smartpossupply.dto.waybill.WaybillPersonDTO;
import uz.uzkassa.smartpossupply.enums.EntityTypeEnum;
import uz.uzkassa.smartpossupply.enums.TransportType;
import uz.uzkassa.smartpossupply.enums.WaybillDeliveryType;
import uz.uzkassa.smartpossupply.enums.WaybillStatus;
import uz.uzkassa.smartpossupply.enums.WaybillType;
import uz.uzkassa.smartpossupply.interfaces.Auditable;

import java.io.Serial;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "waybill",
    indexes = {
        @Index(columnList = "consignor_tin_or_pinfl", name = "waybill_consignor_tin_or_pinfl_idx"),
        @Index(columnList = "consignee_tin_or_pinfl", name = "waybill_consignee_tin_or_pinfl_idx"),
        @Index(columnList = "waybill_no", name = "waybill_waybill_no_idx"),
        @Index(columnList = "waybill_date", name = "waybill_waybill_date_idx"),
        @Index(columnList = "waybill_local_id", name = "waybill_waybill_local_id_idx")
    }
)
@FieldDefaults(level = AccessLevel.PRIVATE)
@SQLDelete(sql = "UPDATE waybill SET deleted = true WHERE id=?")
public class Waybill extends AbstractAuditingEntity implements Auditable {

    @Serial
    private static final long serialVersionUID = 568413989L;

    @Column(name = "waybill_local_id")
    String waybillLocalId;

    @Column(name = "delivery_type")
    WaybillDeliveryType deliveryType;

    @Column(name = "waybill_no")
    String waybillNo;

    @Column(name = "waybill_date")
    LocalDate waybillDate;

    @Column(name = "contract_no")
    String contractNo;

    @Column(name = "contract_date")
    LocalDate contractDate;

    @Column(name = "old_waybill_id")
    String oldWaybillId;

    @Column(name = "old_waybill_no")
    String oldWaybillNo;

    @Column(name = "old_waybill_date")
    LocalDate oldWaybillDate;

    @Column(name = "consignor_tin_or_pinfl")
    String consignorTinOrPinfl;

    @Column(name = "consignor_name")
    String consignorName;

    @Column(name = "consignor_branch_code")
    String consignorBranchCode;

    @Column(name = "consignor_branch_name")
    String consignorBranchName;

    @Column(name = "consignee_tin_or_pinfl")
    String consigneeTinOrPinfl;

    @Column(name = "consignee_name")
    String consigneeName;

    @Column(name = "consignee_branch_code")
    String consigneeBranchCode;

    @Column(name = "consignee_branch_name")
    String consigneeBranchName;

    @Column(name = "freight_forwarder_tin_or_pinfl")
    String freightForwarderTinOrPinfl;

    @Column(name = "freight_forwarder_name")
    String freightForwarderName;

    @Column(name = "freight_forwarder_branch_code")
    String freightForwarderBranchCode;

    @Column(name = "freight_forwarder_branch_name")
    String freightForwarderBranchName;

    @Column(name = "carrier_tin_or_pinfl")
    String carrierTinOrPinfl;

    @Column(name = "carrier_name")
    String carrierName;

    @Column(name = "carrier_branch_code")
    String carrierBranchCode;

    @Column(name = "carrier_branch_name")
    String carrierBranchName;

    @Column(name = "client_id")
    Long clientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", updatable = false, insertable = false)
    Company client;

    @Column(name = "client_branch_id")
    Long clientBranchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_branch_id", updatable = false, insertable = false)
    CustomerBranch clientBranch;

    @Column(name = "client_branch_code")
    String clientBranchCode;

    @Column(name = "client_branch_name")
    String clientBranchName;

    @Column(name = "client_contract_date")
    LocalDate clientContractDate;

    @Column(name = "client_contract_no")
    String clientContractNo;

    @Column(name = "payer_tin_or_pinfl")
    String payerTinOrPinfl;

    @Column(name = "payer_name")
    String payerName;

    @Column(name = "payer_branch_code")
    String payerBranchCode;

    @Column(name = "payer_branch_name")
    String payerBranchName;

    @Column(name = "payer_contract_no")
    String payerContractNo;

    @Column(name = "payer_contract_date")
    LocalDate payerContractDate;

    @Column(name = "responsible_person_pinfl")
    String responsiblePersonPinfl;

    @Column(name = "responsible_person_full_name")
    String responsiblePersonFullName;

    @Column(name = "responsible_company_tin")
    String responsibleCompanyTin;

    @Column(name = "responsible_company_name")
    String responsibleCompanyName;

    @Column(name = "responsible_company_branch_code")
    String responsibleCompanyBranchCode;

    @Column(name = "responsible_company_branch_name")
    String responsibleCompanyBranchName;

    @Column(name = "has_committent", columnDefinition = "boolean default false")
    boolean hasCommittent;

    @Enumerated(EnumType.STRING)
    @Column(name = "transport_type")
    TransportType transportType;

    @Column(name = "total_distance", precision = 25, scale = 2)
    BigDecimal totalDistance;

    @Column(name = "delivery_cost", precision = 25, scale = 2)
    BigDecimal deliveryCost;

    @Column(name = "total_delivery_cost", precision = 25, scale = 2)
    BigDecimal totalDeliveryCost;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    WaybillStatus status;

    @Column(name = "waybill_type")
    @Enumerated(EnumType.STRING)
    WaybillType waybillType;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "signature_content")
    String signatureContent;

    @Column(name = "shipment_id")
    Long shipmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", updatable = false, insertable = false)
    AcceptanceTransferAct shipment;

    @Column(name = "factura_id")
    Long facturaId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", updatable = false, insertable = false)
    Factura factura;

    @Column(name = "stock_transfer_id")
    Long stockTransferId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_transfer_id", updatable = false, insertable = false)
    StockTransfer stockTransfer;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "notes")
    String notes;

    @Column(name = "company_id")
    Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", updatable = false, insertable = false)
    Company company;

    @OneToMany(mappedBy = "waybill", cascade = CascadeType.ALL, orphanRemoval = true)
    List<WaybillExecutor> executors = new ArrayList<>();

    public WaybillLinkDTO toLinkDTO() {
        return new WaybillLinkDTO(this.getId(), this.getWaybillNo(), this.getWaybillLocalId(), this.getStatus(), this.getWaybillDate());
    }

    public SupplyWaybillDTO toDTO(WaybillTransport waybillTransport) {
        SupplyWaybillDTO supplyWaybillDTO = new SupplyWaybillDTO();
        WaybillDTO dto = new WaybillDTO();
        dto.setId(getId());
        if (getWaybillType() != null) {
            supplyWaybillDTO.setWaybillType(getWaybillType());
        }
        dto.setWaybillLocalId(getWaybillLocalId());
        if (getDeliveryType() != null) {
            dto.setDeliveryType(getDeliveryType().getCode());
            supplyWaybillDTO.setDeliveryTypeEnum(getDeliveryType());
        }
        dto.setHasCommittent(isHasCommittent());
        if (getStatus() != null) {
            supplyWaybillDTO.setStatus(getStatus().toDto());
        }
        dto.setTotalDistance(getTotalDistance());
        dto.setDeliveryCost(getDeliveryCost());
        dto.setTotalDeliveryCost(getTotalDeliveryCost());
        if (getTransportType() != null) {
            dto.setTransportType(getTransportType().getCode());
        }

        dto.setWaybillDoc(new WaybillDocDTO(getWaybillNo(), getWaybillDate()));
        if (StringUtils.hasText(getOldWaybillId()) && StringUtils.hasText(getOldWaybillNo()) && getOldWaybillDate() != null) {
            dto.setOldWaybillDoc(new OldWaybillDocDTO(getOldWaybillId(), getOldWaybillNo(), getOldWaybillDate()));
        }
        if (StringUtils.hasText(getContractNo()) && getContractDate() != null) {
            dto.setContractDoc(new WaybillContractDocDTO(getContractNo(), getContractDate()));
        }
        if (StringUtils.hasText(getResponsiblePersonPinfl()) && StringUtils.hasText(getResponsiblePersonFullName())) {
            dto.setResponsiblePerson(new WaybillPersonDTO(getResponsiblePersonPinfl(), getResponsiblePersonFullName()));
        }

        WaybillCompanyDTO consignor = new WaybillCompanyDTO();
        consignor.setName(getConsignorName());
        consignor.setTinOrPinfl(getConsignorTinOrPinfl());
        if (StringUtils.hasText(getConsignorBranchCode())) {
            consignor.setBranchCode(getConsignorBranchCode());
            consignor.setBranchName(getConsignorBranchName());
        }
        dto.setConsignor(consignor);

        WaybillCompanyDTO consignee = new WaybillCompanyDTO();
        consignee.setName(getConsigneeName());
        consignee.setTinOrPinfl(getConsigneeTinOrPinfl());
        if (StringUtils.hasText(getConsigneeBranchCode())) {
            consignee.setBranchCode(getConsigneeBranchCode());
            consignee.setBranchName(getConsigneeBranchName());
        }
        dto.setConsignee(consignee);

        WaybillCompanyDTO freightForwarder = new WaybillCompanyDTO();
        freightForwarder.setName(getFreightForwarderName());
        freightForwarder.setTinOrPinfl(getFreightForwarderTinOrPinfl());
        freightForwarder.setBranchCode(getFreightForwarderBranchCode());
        freightForwarder.setBranchName(getFreightForwarderBranchName());
        dto.setFreightForwarder(freightForwarder);

        WaybillCompanyDTO carrier = new WaybillCompanyDTO();
        carrier.setName(getCarrierName());
        carrier.setTinOrPinfl(getCarrierTinOrPinfl());
        carrier.setBranchCode(getCarrierBranchCode());
        carrier.setBranchName(getCarrierBranchName());
        dto.setCarrier(carrier);

        if (getClient() != null) {
            WaybillClientDTO client = new WaybillClientDTO();
            client.setId(getClientId());
            client.setName(getClient().getName());
            client.setTinOrPinfl(getClient().getTin());
            if (getClientBranch() != null) {
                client.setBranchCode(getClientBranch().getCustomerBranchNumber());
                client.setBranchName(getClientBranch().getName());
            } else {
                client.setBranchCode(getClientBranchCode());
                client.setBranchName(getClientBranchName());
            }
            client.setContractNo(getClientContractNo());
            client.setContractDate(getClientContractDate());
            dto.setClient(client);
        }

        if (StringUtils.hasText(getPayerTinOrPinfl())) {
            WaybillClientDTO payer = new WaybillClientDTO();
            payer.setName(getPayerName());
            payer.setTinOrPinfl(getPayerTinOrPinfl());
            payer.setBranchCode(getPayerBranchCode());
            payer.setBranchName(getPayerBranchName());
            payer.setContractNo(getPayerContractNo());
            payer.setContractDate(getPayerContractDate());
            dto.setPayer(payer);
        }
        if (TransportType.Roadway.equals(getTransportType())) {
            dto.setRoadway(waybillTransport.toDTO());
        } else if (TransportType.Railway.equals(getTransportType())) {
            dto.setRailway(waybillTransport.toDTO());
        }
        for (WaybillExecutor executor : getExecutors()) {
            supplyWaybillDTO.addExecutor(executor.toDTO());
        }
        if (getFactura() != null) {
            supplyWaybillDTO.setFactura(getFactura().toLink());
        }
        if (this.getStockTransfer() != null) {
            supplyWaybillDTO.setStockTransfer(getStockTransfer().toCommonDTO());
        }
        if (getShipment() != null) {
            supplyWaybillDTO.setShipment(getShipment().toLinkDTO());
        }
        supplyWaybillDTO.setNote(getNotes());
        supplyWaybillDTO.setWaybillDTO(dto);
        supplyWaybillDTO.setId(getId());
        return supplyWaybillDTO;
    }

    @Override
    public Long getEntityId() {
        return id;
    }

    @Override
    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.WAY_BILL;
    }

    @Override
    public String getEntityName() {
        return getWaybillNo();
    }
}
