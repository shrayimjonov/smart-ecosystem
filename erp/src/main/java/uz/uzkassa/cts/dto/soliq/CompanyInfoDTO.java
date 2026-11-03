package uz.uzkassa.cts.dto.soliq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyInfoDTO implements Serializable {

    String tin;

    String name;

    String address;

    Long regionId;

    Long districtId;

    BigDecimal balance;

}
