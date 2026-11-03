package uz.uzkassa.smartpossupply.dto.address;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.validator.CommonValidation;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PROTECTED)
public class AddressDTO implements Serializable {

    @NotNull(groups = {CommonValidation.class})
    Long regionId;

    @NotNull(groups = {CommonValidation.class})
    Long districtId;

    @NotNull(groups = {CommonValidation.class})
    String street;

    String house;

    String apartment;

    String longitude;

    String latitude;
}
