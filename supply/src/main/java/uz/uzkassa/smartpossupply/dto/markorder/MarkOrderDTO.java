package uz.uzkassa.smartpossupply.dto.markorder;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.MarkOrderReleaseMethodType;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MarkOrderDTO implements Serializable {

    @Size(min = 1)
    List<MarkOrderProductItemDTO> products;

    MarkOrderReleaseMethodType releaseMethodType = MarkOrderReleaseMethodType.IMPORT;
}
