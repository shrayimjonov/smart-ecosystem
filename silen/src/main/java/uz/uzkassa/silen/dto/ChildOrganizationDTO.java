package uz.uzkassa.silen.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChildOrganizationDTO {
    @NotNull
    private String parentId;

    @NotNull
    private String childId;
}
