package uz.uzkassa.silen.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true, doNotUseGetters = true)
@NoArgsConstructor
@AllArgsConstructor
public class SelectItem implements Serializable {
    private String id;
    private String name;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal amount;

    public SelectItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public SelectItem(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public SelectItem(String id, String name, BigDecimal amount) {
        this.id = id;
        this.name = name;
        this.amount = amount;
    }
}
