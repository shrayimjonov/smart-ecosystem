package uz.uzkassa.silen.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true, doNotUseGetters = true)
@NoArgsConstructor
@AllArgsConstructor
public class ProductSelectCountItem implements Serializable {
    private String type;
    private Integer counter;
}
