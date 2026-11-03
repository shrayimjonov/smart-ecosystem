package uz.uzkassa.smartpos.dto.catalog;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class CategoryTreeDto {
    private Long id;
    private String name;
    private Long parentId;
    private List<CategoryTreeDto> children;

    public void addChildren(CategoryTreeDto child) {
        if (children == null) {
            children = new ArrayList<>();
        }
        children.add(child);
    }

}
