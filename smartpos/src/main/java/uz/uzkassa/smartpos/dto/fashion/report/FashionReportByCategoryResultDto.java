package uz.uzkassa.smartpos.dto.fashion.report;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FashionReportByCategoryResultDto extends FashionReportByCategoryDto {
    private String categoryName;
    private Long parentId;
    private List<FashionReportByCategoryResultDto> children = new ArrayList<>();

    public FashionReportByCategoryResultDto(FashionReportByCategoryDto dto) {
        this.setCategoryId(dto.getCategoryId());
        this.setSalesCount(dto.getSalesCount());
        this.setTotalDiscount(dto.getTotalDiscount());
        this.setTotalVat(dto.getTotalVat());
        this.setTotalSumma(dto.getTotalSumma());
        this.setSalesReceiptsCount(dto.getSalesReceiptsCount());
        this.setReturnedReceiptsCount(dto.getReturnedReceiptsCount());
    }

    public void addChild(FashionReportByCategoryResultDto fashionReportByCategoryResultDto) {
        children.add(fashionReportByCategoryResultDto);
    }
}
