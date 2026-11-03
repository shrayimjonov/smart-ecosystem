package uz.uzkassa.silen.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.silen.enumeration.WaybillType;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WaybillListDTO extends BaseAuditDTO {
    private String id;

    private SelectItem waybillDoc;

    private SelectItem contractDoc;

    private SelectItem consignee;

    private BigDecimal totalDeliveryCost;

    private EnumDTO status;

    private SelectItem shipment;

    private WaybillType waybillType;

    private String waybillTypeName;

    private String comment;
}
