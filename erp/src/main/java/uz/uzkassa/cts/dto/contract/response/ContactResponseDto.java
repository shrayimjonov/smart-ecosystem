package uz.uzkassa.cts.dto.contract.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.ContractStatus;
import uz.uzkassa.cts.enums.ThymeleafTemplateType;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
public class ContactResponseDto implements Serializable {

    private Long Id;
    @NotNull
    private String name;

    @NotNull
    private Long docNumber;
    private LocalDate docDate;
    private LocalDate docEndDate;
    @NotNull
    private ThymeleafTemplateType templateTypeEnum;

    @NotNull
    private String companyName;
    @NotNull
    private String companyAddress;
    @NotNull
    private String companyAccountNumber;
    @NotNull
    private String companyBankName;
    @NotNull
    private String companyBankMFO;
    @NotNull
    private String companyTin;
    @NotNull
    private String companyOKED;
    @NotNull
    private String companyPhoneNumber;
    @NotNull
    private String companyDirectorFullName;

    @NotNull
    private String customerName;
    @NotNull
    private String customerAddress;
    @NotNull
    private String customerAccountNumber;
    @NotNull
    private String customerBankName;
    @NotNull
    private String customerBankMFO;
    @NotNull
    private String customerTin;
    @NotNull
    private String customerOKED;
    @NotNull
    private String customerPhoneNumber;
    @NotNull
    private String customerDirectorFullName;


    @NotNull
    private String fileUri;

    @NotNull
    private String fileName;

    @NotNull
    private String contentType;

    @Enumerated(EnumType.STRING)
    private ContractStatus status;

    @NotNull
    private Long customerId;
    @NotNull
    private Long companyId;

}
