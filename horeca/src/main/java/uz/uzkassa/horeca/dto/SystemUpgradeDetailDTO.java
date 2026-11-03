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
public class SystemUpgradeDetailDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 3325787438L;

    LocalDateTime releaseDate;

    boolean forceUpdate;

    public SystemUpgradeDetailDTO(Long id, String name, LocalDateTime releaseDate, boolean forceUpdate) {
        super(id, name);
        this.releaseDate = releaseDate;
        this.forceUpdate = forceUpdate;
    }
}
