package uz.uzkassa.cts.dto.applicationfile;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.address.base.BaseAddressDTO;
import uz.uzkassa.cts.enums.ApplicationTemplateType;
import uz.uzkassa.cts.enums.DeregisterReason;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ApplicationTemplateDTO implements Serializable {

    Long fiscalizationId;

    String description;

    ApplicationTemplateType templateType;

    String newSerialNumber;

    String fiscalModuleSerialNumber;

    DeregisterReason reason;

    Long ofdFileId;

    LocalDate currentDate = LocalDate.now();

    BaseAddressDTO address;

    Integer neighborhoodCode;

    String neighborhoodName;

    String otherReason;
}
