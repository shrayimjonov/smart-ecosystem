package uz.uzkassa.horeca.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.dto.factura.PlatformType;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class SystemUpgradeDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3325787438L;

    String description;

    boolean available;

    PlatformType platformType;

    LocalDateTime releaseDate;

    boolean forceUpdate;
}
