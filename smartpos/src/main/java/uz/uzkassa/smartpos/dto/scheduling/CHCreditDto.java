package uz.uzkassa.smartpos.dto.scheduling;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CHCreditDto {
    private Long id;
    private Long branchId;
    private boolean forCredit;
    private Integer month;
}
