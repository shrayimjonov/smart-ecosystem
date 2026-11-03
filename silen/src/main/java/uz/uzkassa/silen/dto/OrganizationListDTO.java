package uz.uzkassa.silen.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.OrganizationType;
import uz.uzkassa.silen.integration.billing.enums.BillingDocumentType;

import java.io.Serializable;
import java.util.EnumSet;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrganizationListDTO implements Serializable {

    String id;

    String name;

    @NotNull
    String tin;

    EnumSet<OrganizationType> types;

    String productGroupName;

    String contactPerson;

    String turonToken;

    boolean active;

    BillingDocumentType billingDocumentType;

    boolean xfileBinded;

    BaseUserDTO manager;

}
