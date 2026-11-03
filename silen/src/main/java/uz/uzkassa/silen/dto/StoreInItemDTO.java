package uz.uzkassa.silen.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoreInItemDTO implements Serializable {

    Long id;

    String productId;

    ProductCommonDTO product;

    String storeInId;

    SelectItem storeIn;

    String serialId;

    SelectItem serialNumber;

    Integer qty;

    Set<StoreInMarkDTO> codes;
}
