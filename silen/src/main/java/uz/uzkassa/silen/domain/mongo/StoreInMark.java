package uz.uzkassa.silen.domain.mongo;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import uz.uzkassa.silen.dto.StoreInMarkDTO;
import uz.uzkassa.silen.enumeration.PackageType;

import java.math.BigDecimal;

@ToString
@Getter
@Setter
@Document(collection = "store_in_mark")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreInMark {

    @Id
    String id;

    String code;

    String printCode;

    @Indexed
    Long itemId;

    @Indexed
    String shipmentId;

    Integer unit;

    BigDecimal capacity;

    public StoreInMarkDTO toDTO() {
        StoreInMarkDTO dto = new StoreInMarkDTO();
        dto.setId(getId());
        dto.setCode(getCode());
        dto.setPrintCode(getPrintCode());
        dto.setUnit(PackageType.fromCode(getUnit()));
        dto.setItemId(getItemId());
        return dto;
    }

    public PackageType getPackageType() {
        return unit != null ? PackageType.fromCode(unit) : PackageType.BOTTLE;
    }
}
