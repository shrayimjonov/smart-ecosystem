package uz.uzkassa.cts.dto.contract.request;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.ThymeleafTemplateType;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@EqualsAndHashCode
public class ContractFilterRequestDto implements Serializable {

    private String name;
    private Long docNumber;
    private LocalDate docDate;
    private LocalDate docEndDate;
    private ThymeleafTemplateType templateTypeEnum;
    private String customerName;
    private Long customerId;
    private String customerAddress;
    private String customerAccountNumber;
    private String customerTin;
    private String customerPhoneNumber;
    private String customerDirectorFullName;
    private ContractStatus status;

}
