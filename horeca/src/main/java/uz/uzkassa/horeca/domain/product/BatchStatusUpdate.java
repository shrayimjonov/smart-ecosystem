package uz.uzkassa.horeca.domain.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.horeca.enums.CommonStatus;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class BatchStatusUpdate implements Serializable {

    @Size(min = 1)
    private List<Long> ids;

    @NotNull
    private CommonStatus status;
}
