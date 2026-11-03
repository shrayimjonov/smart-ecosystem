package uz.uzkassa.smartpos.dto.product;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class VariationValue implements Serializable {
    private String code;
    private String value;
}
