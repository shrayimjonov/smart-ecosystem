package uz.uzkassa.smartpos.dto.warehouse;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class IncomeOfProductDetailValidationDTO {
    private Long id;
    private String productName;
    private String productBarcode;
    private String vatBarcode;
    private BigDecimal exciseRate;
    private BigDecimal excise;
    private BigDecimal price;
    private BigDecimal amount;
    private BigDecimal ndsPercent;
    private BigDecimal nds;
    private BigDecimal salesPrice;
    private BigDecimal qty;
    private Long unitId;
    private List<MarkingValidationDTO> codes = new ArrayList<>();
    private String errorMessage;
    private boolean exist;

    public String getErrorMessage() {
        return isExist() ?
            CollectionUtils.isNotEmpty(codes) && codes.stream().allMatch(dto -> StringUtils.isNotBlank(dto.getErrorMessage())) ?
                "Ошибка в маркировке товара" :
                null :
            "Данного товара нет в каталоге";
    }
}
