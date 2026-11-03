package uz.uzkassa.cts.dto.soliq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyBankInfoDTO implements Serializable {

    Long id;

    String name;

    String code;//MFO

    String accountNumber;

    String accountant;

    String oked;
}
