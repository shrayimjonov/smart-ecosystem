package uz.uzkassa.cts.dto.customer.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class RegionItemDTO extends CommonDTO {

    String shortName;

    public RegionItemDTO(Long id, String name, String shortName) {
        super(id, name);
        this.shortName = shortName;
    }
}
