package uz.uzkassa.silen.dto.waybill;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CompanyTransportDTO {
    private String regNo;
    private String model;
    private Integer ownershipType;
    private Integer transportType;
}
