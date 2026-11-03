package uz.uzkassa.cts.dto.ctsservice.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.cts.enums.ServiceRegistrationType;
import uz.uzkassa.cts.enums.ServiceType;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ServiceResponseDto implements Serializable {

    private Long id;
    private String name;
    private BigDecimal price;
    private BigDecimal bonus;
    @Enumerated(EnumType.STRING)
    private ServiceType serviceType;
    @Enumerated(EnumType.STRING)
    private ServiceRegistrationType registrationType;
    private Boolean withQuantity;
    private Boolean forXfile;
    private String code;
    private String catalogCode;
}
