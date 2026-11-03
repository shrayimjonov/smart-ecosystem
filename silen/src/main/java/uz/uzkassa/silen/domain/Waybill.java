package uz.uzkassa.silen.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import uz.uzkassa.silen.config.DbConstants;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.WaybillListDTO;
import uz.uzkassa.silen.dto.waybill.*;
import uz.uzkassa.silen.enumeration.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@Entity
@Cacheable
@Table(schema = DbConstants.CURRENT_SCHEMA, name = "waybill")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@SQLDelete(sql = "UPDATE waybill SET deleted = 'true' WHERE id=?")
public class Waybill extends BaseOrganizationEntity implements Serializable {

    @Column(name = "waybill_local_id")
    String waybillLocalId;

    @Column(name = "waybill_local_type", columnDefinition = "integer default 0")
    Integer waybillLocalType;

    @Column(name = "delivery_type")
    DeliveryType deliveryType;
    @Column(name = "waybill_no")
    String waybillNo;
    @Column(name = "waybill_date")
    LocalDate waybillDate;
    @Column(name = "contract_no")
    String contractNo;
    @Column(name = "contract_date")
    LocalDate contractDate;

    @Column(name = "old_waybill_local_id")
    String oldWaybillLocalId;
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

    @Column(name = "client_tin_or_pinfl")
    String clientTinOrPinfl;
    @Column(name = "client_name")
    String clientName;
    @Column(name = "client_branch_code")
    String clientBranchCode;
    @Column(name = "client_branch_name")
    String clientBranchName;
    @Column(name = "client_contract_no")
    String clientContractNo;
    @Column(name = "client_contract_date")
    LocalDate clientContractDate;

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

    @JdbcTypeCode(Types.LONGVARCHAR)
    @Column(name = "signature_content")
    private String signatureContent;

    @OneToOne(mappedBy = "waybill", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    WaybillTransport waybillTransport;

    @Column(name = "shipment_id")
    String shipmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipment_id", updatable = false, insertable = false)
    Shipment shipment;

    @Column(name = "notes", columnDefinition = "text")
    String notes;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "waybill", cascade = CascadeType.ALL, orphanRemoval = true)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    List<WaybillExecutor> executors = new ArrayList<>();

    @Column(name = "invoice_id")
    Long invoiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", insertable = false, updatable = false)
    ListInvoice invoice;

    @Column(name = "act_id")
    String acceptanceTransferActId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "act_id", insertable = false, updatable = false)
    AcceptanceTransferAct acceptanceTransferAct;

    @Column(name = "comment")
    String comment;


    public void addWaybillTransport(WaybillTransport waybillTransport) {
        waybillTransport.setWaybill(this);
        this.setWaybillTransport(waybillTransport);
    }
    public void addExecutor(WaybillExecutor waybillExecutor) {
        waybillExecutor.setWaybill(this);
        this.getExecutors().add(waybillExecutor);
    }

    public WaybillListDTO toListDTO() {
        WaybillListDTO dto = new WaybillListDTO();
        dto.setId(getId());
        dto.setComment(getComment());
        if (getWaybillType() != null) {
            dto.setWaybillType(getWaybillType());
            dto.setWaybillTypeName(getWaybillType().getRuText());
        }
        dto.setWaybillDoc(new SelectItem(null, getWaybillNo(), Optional.ofNullable(getWaybillDate()).orElse(LocalDate.now()).toString()));
        dto.setContractDoc(new SelectItem(null, getContractNo(), Optional.ofNullable(getContractDate()).orElse(LocalDate.now()).toString()));
        dto.setConsignee(new SelectItem(getConsigneeTinOrPinfl(), getConsigneeName()));
        dto.setTotalDeliveryCost(getTotalDeliveryCost());
        if (getStatus() != null) {
            dto.setStatus(getStatus().toListDto());
        }
        if (getShipment() != null) {
            dto.setShipment(getShipment().toSelectItem());
        }
        setAuditValues(dto);
        return dto;
    }

    public WaybillDTO toDTO() {
        WaybillDTO dto = new WaybillDTO();
        dto.setId(getId());
        dto.setComment(getComment());
        if (getWaybillType() != null) {
            dto.setWaybillType(getWaybillType());
            dto.setWaybillTypeName(getWaybillType().getRuText());
        }
        dto.setWaybillLocalId(getWaybillLocalId());
        dto.setWaybillLocalType(getWaybillLocalType());
        WaybillLocalType waybillLocalType1 = WaybillLocalType.fromCode(getWaybillLocalType());
        dto.setWaybillLocalTypeName(waybillLocalType1.getText());
        if (getOldWaybillLocalId() != null) {
            WaybillDocOld waybillDocOld = new WaybillDocOld();
            waybillDocOld.setOldWaybillLocalId(getOldWaybillLocalId());
            waybillDocOld.setOldWaybillNo(getOldWaybillNo());
            waybillDocOld.setOldWaybillDate(getOldWaybillDate());
            dto.setOldWaybillDoc(waybillDocOld);
        }
        if (getDeliveryType() != null) {
            dto.setDeliveryType(getDeliveryType().getCode());
            dto.setDeliveryTypeEnum(getDeliveryType());
            dto.setDeliveryTypeEnumName(getDeliveryType().getText());
        }
        dto.setHasCommittent(isHasCommittent());
        if (getStatus() != null) {
            dto.setStatus(getStatus());
            dto.setStatusName(getStatus().getRuText());
        }
        dto.setTotalDistance(getTotalDistance());
        dto.setDeliveryCost(getDeliveryCost());
        dto.setTotalDeliveryCost(getTotalDeliveryCost());
        if (getTransportType() != null) {
            dto.setTransportType(getTransportType().getCode());
        }

        dto.setWaybillDoc(new WaybillDoc(getWaybillNo(), getWaybillDate()));
        if (getContractNo() != null && getContractDate() != null) {
            dto.setContractDoc(new ContractDoc(getContractNo(), getContractDate()));
        }
        if (getResponsiblePersonPinfl() != null) {
            dto.setResponsiblePerson(new PersonDTO(getResponsiblePersonPinfl(), getResponsiblePersonFullName()));
        }

        WaybillCompany consignor = new WaybillCompany();
        consignor.setName(getConsignorName());
        consignor.setTinOrPinfl(getConsignorTinOrPinfl());
        consignor.setBranchCode(getConsignorBranchCode());
        consignor.setBranchName(getConsignorBranchName());
        dto.setConsignor(consignor);

        if (getConsigneeName() != null && getConsigneeTinOrPinfl() != null) {
            WaybillCompany consignee = new WaybillCompany();
            consignee.setName(getConsigneeName());
            consignee.setTinOrPinfl(getConsigneeTinOrPinfl());
            consignee.setBranchCode(getConsigneeBranchCode());
            consignee.setBranchName(getConsigneeBranchName());
            dto.setConsignee(consignee);
        }

        if (getFreightForwarderTinOrPinfl() != null) {
            WaybillCompany freightForwarder = new WaybillCompany();
            freightForwarder.setName(getFreightForwarderName());
            freightForwarder.setTinOrPinfl(getFreightForwarderTinOrPinfl());
            freightForwarder.setBranchCode(getFreightForwarderBranchCode());
            freightForwarder.setBranchName(getFreightForwarderBranchName());
            dto.setFreightForwarder(freightForwarder);
        }

        if (getCarrierTinOrPinfl() != null) {
            WaybillCompany carrier = new WaybillCompany();
            carrier.setName(getCarrierName());
            carrier.setTinOrPinfl(getCarrierTinOrPinfl());
            carrier.setBranchCode(getCarrierBranchCode());
            carrier.setBranchName(getCarrierBranchName());
            dto.setCarrier(carrier);
        }

        if (getClientTinOrPinfl() != null) {
            WaybillClient client = new WaybillClient();
            client.setName(getClientName());
            client.setTinOrPinfl(getClientTinOrPinfl());
            client.setBranchCode(getClientBranchCode());
            client.setBranchName(getClientBranchName());
            client.setContractNo(getClientContractNo());
            client.setContractDate(getClientContractDate());
            dto.setClient(client);
        }

        if (getPayerTinOrPinfl() != null) {
            WaybillClient payer = new WaybillClient();
            payer.setName(getPayerName());
            payer.setTinOrPinfl(getPayerTinOrPinfl());
            payer.setBranchCode(getPayerBranchCode());
            payer.setBranchName(getPayerBranchName());
            payer.setContractNo(getPayerContractNo());
            payer.setContractDate(getPayerContractDate());
            dto.setPayer(payer);
        }
        if (getWaybillTransport() != null) {
            if (TransportType.Roadway == getTransportType()) {
                dto.setRoadway(getWaybillTransport().toRoadwayDTO());
            }
            if (TransportType.Railway == getTransportType()) {
                dto.setRailway(getWaybillTransport().toRailwayDTO());
            }
        }
        dto.setNote(getNotes());
        if (CollectionUtils.isNotEmpty(getExecutors())) {
            List<WaybillExecutorDTO> list = new ArrayList<>();
            for (WaybillExecutor executor : getExecutors()) {
                list.add(executor.toDTO());
            }
            dto.setExecutors(list);
        }
        return dto;
    }

}
