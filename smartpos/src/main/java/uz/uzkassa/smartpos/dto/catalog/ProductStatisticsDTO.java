package uz.uzkassa.smartpos.dto.catalog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductStatisticsDTO {
    private Long totalCount;
    private Long favoriteCount;
    private Long creditCount;
    private Long advanceCount;
}
