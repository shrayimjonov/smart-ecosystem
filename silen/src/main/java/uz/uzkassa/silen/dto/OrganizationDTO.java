package uz.uzkassa.silen.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.AggregationMode;
import uz.uzkassa.silen.enumeration.OrganizationType;
import uz.uzkassa.silen.enumeration.ProductGroup;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrganizationDTO implements Serializable {

    String id;

    String name;

    @NotNull
    String tin;

    EnumSet<OrganizationType> types;

    Long regionId;

    String regionName;

    String districtId;

    String districtName;

    String address;

    String mobile;

    String workPhone;

    String account;

    String bankId;

    String bankName;

    String oked;

    String director;

    String accountant;

    String website;

    String email;

    Double lat;

    Double lon;

    @NotNull
    Set<String> gcp;

    String turonToken;

    String omsId;

    String trueToken;

    ProductGroup productGroup;

    String productGroupName;

    boolean demoAllowed;

    String contactPerson;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String factoryId;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String factoryCountry;

    boolean active;

    LocalDate trialPeriod;
    boolean xfileBinded;
    boolean hasChild;
    LocalDateTime parentDate;

    String managerId;

    AggregationMode aggregationMode;
    String aggregationModeName;

}
