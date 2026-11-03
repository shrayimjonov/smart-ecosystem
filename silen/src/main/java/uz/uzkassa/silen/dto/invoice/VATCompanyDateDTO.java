package uz.uzkassa.silen.dto.invoice;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class VATCompanyDateDTO implements Serializable {
    boolean success;
    String reason;
    CompanyVatDTO data;
}
