package uz.uzkassa.cts.dto.kapitalbank.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.dto.ledger.response.CurrentStatementResponseDto;
import uz.uzkassa.cts.enums.CustomerAppType;

@Getter
@Setter
public class CustomerCheckResponseDto extends CurrentStatementResponseDto {
    private Long deviceCount;
    private String tin;
    private String customerName;
    private CustomerAppType customerAppType;
}
