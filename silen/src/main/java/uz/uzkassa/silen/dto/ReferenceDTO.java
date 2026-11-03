package uz.uzkassa.silen.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.Reference} entity.
 */
@Getter
@Setter
@ToString
public class ReferenceDTO implements Serializable {
    private String id;
    @NotNull
    private String code;
    @NotNull
    private String name;
    @NotNull
    private String parentId;
    private String parentName;
}
