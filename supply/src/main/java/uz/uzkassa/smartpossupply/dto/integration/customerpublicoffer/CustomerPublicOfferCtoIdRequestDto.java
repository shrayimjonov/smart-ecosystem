package uz.uzkassa.smartpossupply.dto.integration.customerpublicoffer;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerPublicOfferCtoIdRequestDto {

    @NotNull
    @NotEmpty
    private String publicOfferId;

    @NotNull
    private String tin;

    private String customerOKED;
    private String accountNumber;
    private String name;

    private String region;

    private String district;

    private String street;

    private String house;

    private String apartment;

    private String phone;
}
