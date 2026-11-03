package uz.uzkassa.cts.dto.soliq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyInfoResultDTO implements Serializable {

    CompanyInfoDTO company;

    CompanyOwnerDTO owner;

}
