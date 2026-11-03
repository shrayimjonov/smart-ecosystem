package uz.uzkassa.smartpos.dto.catalog;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryListDto {
    private Long id;
    private String name;
    private Long parentId;
    private Long productCount;
    private Long advanceCount;
    private Long creditCount;
    private String mxikCode;
    private List<CategoryListDto> children;

    public CategoryListDto(CategoryResult categoryResult) {
        this.id = categoryResult.id();
        this.name = categoryResult.name();
        this.parentId = categoryResult.parentId();
        this.productCount = categoryResult.productCount();
        this.advanceCount = categoryResult.advanceCount();
        this.creditCount = categoryResult.creditCount();
        this.mxikCode = categoryResult.mxikCode();
    }

    public void addChildren(CategoryListDto category) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        children.add(category);
    }
}
