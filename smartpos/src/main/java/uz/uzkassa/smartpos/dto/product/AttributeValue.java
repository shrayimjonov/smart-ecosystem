package uz.uzkassa.smartpos.dto.product;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(of = {"key", "code"})
@Builder
public class AttributeValue implements Serializable {
    private String key;
    private String code;
    private String value;
    private Integer sorder;
}
