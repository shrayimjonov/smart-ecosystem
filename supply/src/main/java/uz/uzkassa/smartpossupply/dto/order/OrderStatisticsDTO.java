package uz.uzkassa.smartpossupply.dto.order;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 19.12.2022
 */
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderStatisticsDTO {

    Long newTotal;

    Long draftTotal;

    Long inProgressTotal;

    Long approvedTotal;

    Long rejectedTotal;

    Long completedTotal;

    public OrderStatisticsDTO(
        Long newTotal,
        Long draftTotal,
        Long inProgressTotal,
        Long approvedTotal,
        Long rejectedTotal,
        Long completedTotal
    ) {
        this.newTotal = newTotal;
        this.draftTotal = draftTotal;
        this.inProgressTotal = inProgressTotal;
        this.approvedTotal = approvedTotal;
        this.rejectedTotal = rejectedTotal;
        this.completedTotal = completedTotal;
    }
}
