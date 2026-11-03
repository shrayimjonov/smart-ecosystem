package uz.uzkassa.silen.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.dto.invoice.LgotaDTO;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductDetail {

    String barcode;

    String parentBarcode;

    String productName;

    BigDecimal price;

    String catalogCode;

    String catalogName;

    int lgotaType;

    List<LgotaDTO> lgotas;

    int origin;

    String originName;

    boolean hasMedical;
}
