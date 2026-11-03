package uz.uzkassa.smartpos.dto.filter;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
public class ImportProductsFilter implements Serializable {
    @ToString.Include
    private Long branchId;

    @ToString.Include
    private Long categoryId;

}
