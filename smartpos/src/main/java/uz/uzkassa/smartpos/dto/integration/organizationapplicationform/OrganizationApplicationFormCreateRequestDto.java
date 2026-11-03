package uz.uzkassa.smartpos.dto.integration.organizationapplicationform;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class OrganizationApplicationFormCreateRequestDto implements Serializable {

    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String phone;

    @NotNull
    @NotEmpty
    private String tin;

    @NotNull
    @NotEmpty
    private String data;
}

