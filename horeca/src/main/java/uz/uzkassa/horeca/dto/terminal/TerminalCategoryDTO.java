package uz.uzkassa.horeca.dto.terminal;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
public class TerminalCategoryDTO {
    private Long id;
    private String name;
    private Long parentId;
    private boolean enabled;
    private Long productCount;
    private Long creditCount;
    private Long advanceCount;
    private TerminalCategorySalesStats salesStats;
    private List<TerminalCategoryDTO> children = new ArrayList<>();
    private Long branchId;

    public TerminalCategoryDTO(Long id) {
        this.id = id;
    }

    public TerminalCategoryDTO(Long id, String name) {
        this.id = id;
        this.name = name;
        this.enabled = true;
    }

    public TerminalCategoryDTO(Long id, String name, Long parentId) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
    }

    /*used for native query*/
    public TerminalCategoryDTO(Long id, String name, Long parentId, Boolean enabled, Long productCount, Long creditCount, Long advanceCount) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.enabled = Boolean.TRUE.equals(enabled);
        this.productCount = productCount;
        this.creditCount = creditCount;
        this.advanceCount = advanceCount;
    }
}
