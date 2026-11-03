package uz.uzkassa.cts.dto.customer;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
public class CustomerFiscalModuleTemplateDTO implements Serializable {
    String companyName;
    String owner;
    String fiscalModuleSerial;
    String businessType;
    String bankName;
    String mfo;
    String accountNumber;
    String customerTin;
    LocalDateTime date;
}
