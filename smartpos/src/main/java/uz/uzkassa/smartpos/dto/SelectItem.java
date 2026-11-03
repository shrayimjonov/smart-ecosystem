package uz.uzkassa.smartpos.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.base.CommonDTO;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class SelectItem extends CommonDTO implements Serializable {
    private String description;
    private BigDecimal price;
    private Boolean selected;

    public SelectItem(Long id, String name) {
        this(id, name, null, null);
    }

    public SelectItem(Long id, String name, String description) {
        this(id, name, description, null);
    }

    public SelectItem(Long id, String name, String description, BigDecimal price) {
        this(id, name, description, price, null);
    }

    public SelectItem(Long id, String name, String description, BigDecimal price, Boolean selected) {
        super(id, name);
        this.description = description;
        this.price = price;
        this.selected = selected;
    }

    public SelectItem(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
