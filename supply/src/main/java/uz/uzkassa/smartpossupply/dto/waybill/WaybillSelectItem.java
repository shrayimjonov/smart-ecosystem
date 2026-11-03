package uz.uzkassa.smartpossupply.dto.waybill;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true, doNotUseGetters = true)
@NoArgsConstructor
@AllArgsConstructor
public class WaybillSelectItem implements Serializable {
    private Long id;
    private String name;
    private String tin;
    private LocalDate date;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal amount;

    public WaybillSelectItem(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public WaybillSelectItem(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public WaybillSelectItem(Long id, String name, LocalDate date) {
        this.id = id;
        this.name = name;
        this.date = date;
    }

    public WaybillSelectItem(String name, String tin) {
        this.name = name;
        this.tin = tin;
    }
}
