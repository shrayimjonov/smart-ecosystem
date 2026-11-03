package uz.uzkassa.silen.dto.marking;

import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.enumeration.ReleaseMethodType;

import java.io.Serializable;
import java.util.Set;

/**
 * A DTO for the {@link uz.uzkassa.silen.domain.Order} entity.
 */
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderDTO implements Serializable {
    @Size(min = 1)
    Set<OrderProductItemDTO> products;

/*    @ApiModelProperty("this property required for only \"ProductGroup.Alcohol\"")
    CreateMethodType createMethodType = CreateMethodType.SELF_MADE;*/

    ReleaseMethodType releaseMethodType = ReleaseMethodType.PRODUCTION;

    String description;
}
