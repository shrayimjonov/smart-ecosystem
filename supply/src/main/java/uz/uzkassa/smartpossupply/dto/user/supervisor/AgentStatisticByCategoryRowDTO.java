package uz.uzkassa.smartpossupply.dto.user.supervisor;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class AgentStatisticByCategoryRowDTO {
    Long agentId;
    Long categoryId;
    String categoryName;
    Long unitId;
    String unitName;
    BigDecimal totalBrutto;
    BigDecimal totalNetto;
    BigDecimal totalQty;
    Long totalQtyInPackage;

    public AgentStatisticDTO toAgentStatisticDTO() {
        return new AgentStatisticDTO(categoryId, categoryName, unitId, unitName, totalBrutto, totalNetto, totalQty, totalQtyInPackage);
    }
}
