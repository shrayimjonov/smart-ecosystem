package uz.uzkassa.cts.dto.position;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.LocalizedDTO;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PositionDTO extends LocalizedDTO {

    Long parentId;

    Long companyId;
}
