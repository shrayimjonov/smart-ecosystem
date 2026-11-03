package uz.uzkassa.smartpossupply.dto.company;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CompanyAggregationInfoDTO implements Serializable {

    Long id;

    Long companyId;

    String gcp;

    String turonToken;

    String omsId;

    String contactPerson;

    String aslBelgiToken;

    LocalDateTime aslBelgiTokenDate;

    String factoryId;

    String factoryCountry;
}
