package uz.uzkassa.smartpos.dto.integration.organizationapplicationform;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class OrganizationApplicationFormArchiveRequestDto implements Serializable {

    @NotNull
    @NotEmpty
    private String id;

    @NotNull
    private Boolean archive;
}
