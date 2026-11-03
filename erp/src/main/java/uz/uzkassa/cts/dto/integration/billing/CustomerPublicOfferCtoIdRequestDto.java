package uz.uzkassa.cts.dto.integration.billing;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.cts.enums.billing.BillingDocumentType;

import java.time.LocalDate;

@Getter
@Setter
public class CustomerPublicOfferCtoIdRequestDto {
    @NotNull
    @NotEmpty
    private String publicOfferId;
    @NotNull
    private String tin;

    //    @NotNull
//    @NotEmpty
    private String customerOKED;
    private String accountNumber;
    private String name;
    //    @NotNull
//    @NotEmpty
    private String region;
    //    @NotNull
//    @NotEmpty
    private String district;
    //    @NotNull
//    @NotEmpty
    private String street;
    //    @NotNull
//    @NotEmpty
    private String house;
    //    @NotNull
//    @NotEmpty
    private String apartment;
    //    @NotNull
//    @NotEmpty
    private String phone;
    private BillingDocumentType documentType = BillingDocumentType.PUBLIC_OFFER;
    private String contractNumber;
    private LocalDate contractDate;
}

