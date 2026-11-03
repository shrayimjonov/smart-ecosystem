package uz.uzkassa.horeca.dto.eps;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.uzkassa.horeca.enums.terminal.EpsServiceStatusEnum;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class EpsServiceCrudDTO implements Serializable {

    @NotBlank
    private String name;

    @NotBlank
    private String type;

    private EpsServiceStatusEnum status;
    private Long fileId;
    private boolean defaultService;

    @NotNull
    private Short position;
}
