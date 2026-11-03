package uz.uzkassa.cts.dto.tariff.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.cts.dto.ctsservice.response.ServiceResponseDto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TariffResponseDto implements Serializable {


    private Long id;
    private String name;
    private Double amount;
    private Double discount;
    private Integer period;
    private String discountType;
    private String periodType;
    private String tariffSpecialType;
    private List<ServiceResponseDto> services = new ArrayList<>();
}
