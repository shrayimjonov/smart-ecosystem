package uz.uzkassa.silen.dto.invoice;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CompanyDTO implements Serializable {
    String tin;
    String name;
    String address;
    String regCode;
    String regDate;
    String dateFrom;
    String type;
    String dateCreate;
}
