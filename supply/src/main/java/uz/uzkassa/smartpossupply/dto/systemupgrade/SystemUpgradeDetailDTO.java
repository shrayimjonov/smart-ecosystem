package uz.uzkassa.smartpossupply.dto.systemupgrade;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.io.Serial;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 26.11.2024
 */
@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class SystemUpgradeDetailDTO extends CommonDTO {

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
