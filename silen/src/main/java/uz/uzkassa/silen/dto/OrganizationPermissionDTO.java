package uz.uzkassa.silen.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationPermissionDTO {

    String serviceType;

    String serviceTypeName;

    Integer count;
}
