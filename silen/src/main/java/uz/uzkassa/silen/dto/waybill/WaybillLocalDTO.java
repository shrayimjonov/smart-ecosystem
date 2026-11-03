package uz.uzkassa.silen.dto.waybill;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.dto.OrganizationDTO;
import uz.uzkassa.silen.enumeration.DeliveryType;
import uz.uzkassa.silen.enumeration.TrailerType;
import uz.uzkassa.silen.enumeration.WaybillStatus;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WaybillLocalDTO {

    String id;

    String waybillLocalId;

    DeliveryType deliveryType;
    String waybillNo;
    LocalDate waybillDate;
    String contractNo;
    LocalDate contractDate;

    String consignorId;

    OrganizationDTO consignor;

    String consignorBranchCode;
    String consignorBranchName;

    String consigneeId;

    OrganizationDTO consignee;

    String consigneeBranchCode;
    String consigneeBranchName;
    String freightForwarderId;

    Organization freightForwarder;

    String freightForwarderBranchCode;
    String freightForwarderBranchName;

    String carrierId;

    Organization carrier;

    String carrierBranchCode;
    String carrierBranchName;

    String clientId;

    Organization client;

    String clientBranchCode;
    String clientBranchName;

    String clientContractNo;
    LocalDate clientContractDate;

    String payerId;

    Organization payer;

    String payerBranchCode;
    String payerBranchName;

    String payerContractNo;
    LocalDate payerContractDate;

    String responsiblePersonPinfl;
    String responsiblePersonFullName;

    Integer transportType;

    String driverPinfl;
    String driverName;

    String truckRegNo;
    String truckModel;

    String trailerRegNo;
    String trailerModel;
    TrailerType trailerType;

    BigDecimal totalDistance;
    BigDecimal deliveryCost;
    BigDecimal totalDeliveryCost;

    BigDecimal totalDeliverySum;
    BigDecimal totalWeightBrutto;

    boolean hasCommittent;

    WaybillStatus status;
}
