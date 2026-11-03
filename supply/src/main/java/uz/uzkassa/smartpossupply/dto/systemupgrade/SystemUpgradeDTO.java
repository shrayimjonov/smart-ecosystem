package uz.uzkassa.smartpossupply.dto.systemupgrade;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.PlatformType;

import java.io.Serial;
import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;

@Setter
@Getter
@FieldDefaults(level = PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class SystemUpgradeDTO extends CommonDTO {

    @Serial
    private static final long serialVersionUID = 3325787438L;

    String description;

    boolean available;

    PlatformType platformType;

    LocalDateTime releaseDate;

    boolean forceUpdate;

    Long postId;
}
