package uz.uzkassa.cts.dto.rentact.response;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.dto.rentactaccessory.response.RentActAccessoryResponseDto;

import java.util.Set;

@Getter
@Setter
public class RentActResponseDto extends RentActListResponseDto {
    private Set<RentActAccessoryResponseDto> accessories;
}
