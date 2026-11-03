package uz.uzkassa.smartpos.dto.catalog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryProductCountPayload {
    private Long count;
    private Long creditCount;
    private Long advanceCount;
}
