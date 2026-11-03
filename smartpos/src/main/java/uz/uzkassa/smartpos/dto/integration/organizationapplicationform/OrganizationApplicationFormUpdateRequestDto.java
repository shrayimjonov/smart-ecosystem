package uz.uzkassa.smartpos.dto.integration.organizationapplicationform;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.integration.enums.OrganizationApplicationFormStatus;

import java.io.Serializable;

@Getter
@Setter
public class OrganizationApplicationFormUpdateRequestDto implements Serializable {

    @NotNull
    @NotEmpty
    private String id;

    private OrganizationApplicationFormStatus status;

    private String comment;
}

