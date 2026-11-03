package uz.uzkassa.smartpos.dto.warehouse;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.smartpos.integration.trueApi.TuronWithdrawRequestDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawRequestDTO extends TuronWithdrawRequestDTO {
    private Long id;
}
