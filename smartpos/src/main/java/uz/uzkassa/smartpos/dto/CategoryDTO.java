package uz.uzkassa.smartpos.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.analytics.CategorySalesStats;
import uz.uzkassa.smartpos.enums.CompanyType;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class CategoryDTO {
    private Long id;
    private String name;
    private String nameUz;
    private Long parentId;
    private boolean enabled;
    private Long productCount;
    private Long creditCount;
    private Long advanceCount;
    private CategorySalesStats salesStats;
    private List<CategoryDTO> children = new ArrayList<>();
    private Long branchId;
    private CompanyType companyType;
    private String mxikCode;

    public CategoryDTO(Long id) {
        this.id = id;
    }

    public CategoryDTO(Long id, String name) {
        this.id = id;
        this.name = name;
        this.enabled = true;
    }

    public CategoryDTO(Long id, String name, Long parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }

    /*used for native query*/
    public CategoryDTO(Long id, String name, Long parentId, Boolean enabled, Long productCount, Long creditCount, Long advanceCount) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.enabled = Boolean.TRUE.equals(enabled);
        this.productCount = productCount;
        this.creditCount = creditCount;
        this.advanceCount = advanceCount;
    }
}
