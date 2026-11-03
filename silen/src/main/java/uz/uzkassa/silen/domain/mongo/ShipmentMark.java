package uz.uzkassa.silen.domain.mongo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import uz.uzkassa.silen.dto.mongo.ShipmentMarkDTO;
import uz.uzkassa.silen.enumeration.PackageType;

@ToString
@Getter
@Setter
@Document(collection = "shipment_mark")
public class ShipmentMark {
    @Id
    private String id;
    @Indexed(unique = true)
    private String code;
    @Indexed(unique = true)
    private String printCode;
    @Indexed
    private Long shipmentItemId;
    private Integer unit;
    private int qty;

    public ShipmentMarkDTO toDto() {
        ShipmentMarkDTO dto = new ShipmentMarkDTO();
        dto.setId(getId());
        dto.setCode(getCode());
        dto.setPrintCode(getPrintCode());
        dto.setShipmentItemId(getShipmentItemId());
        dto.setUnit(getPackageType());
        dto.setQuantity(getQty());
        return dto;
    }

    public PackageType getPackageType() {
        return getUnit() == null ? PackageType.BOTTLE : PackageType.fromCode(unit);
    }
    public ShipmentMarkDTO toShortDto() {
        ShipmentMarkDTO dto = new ShipmentMarkDTO();
        dto.setPrintCode(getPrintCode());
        dto.setUnit(getPackageType());
        dto.setQuantity(getQty());
        return dto;
    }
}
