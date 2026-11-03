package uz.uzkassa.cts.dto.rentactofreturn.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.dto.rentactaccessory.response.RentActAccessoryResponseDto;

import java.util.Set;

@Getter
@Setter
public class RentActOfReturnResponseDto extends RentActOfReturnListResponseDto {
    private Set<RentActAccessoryResponseDto> accessories;
}
