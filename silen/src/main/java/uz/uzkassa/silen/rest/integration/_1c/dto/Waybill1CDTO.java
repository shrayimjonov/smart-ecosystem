package uz.uzkassa.silen.rest.integration._1c.dto;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.silen.dto.waybill.ContractDoc;
import uz.uzkassa.silen.dto.waybill.WaybillCompany;
import uz.uzkassa.silen.dto.waybill.WaybillDTO;
import uz.uzkassa.silen.dto.waybill.WaybillDoc;
import uz.uzkassa.silen.enumeration.DeliveryType;
import uz.uzkassa.silen.enumeration.WaybillLocalType;
import uz.uzkassa.silen.enumeration.WaybillStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class Waybill1CDTO {

    private String id;

    private String waybillLocalId;

    private WaybillDoc waybillDoc;

    private ContractDoc contractDoc;

    private DeliveryType deliveryType;

    private String deliveryTypeName;

    private WaybillCompany consignee;

    private WaybillCompany consignor;

    private WaybillStatus status;

    private String statusName;

    private Integer waybillType;

    private WaybillLocalType waybillTypeName;

    private WaybillDTO waybillDTO;

    LocalDateTime createdDate;

    LocalDateTime lastModifiedDate;
}
