package uz.uzkassa.cts.dto.reference.admin;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.LocalizedDTO;
import uz.uzkassa.cts.enums.ReferenceType;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ReferenceDetailAdminDTO extends LocalizedDTO implements Serializable {

    ReferenceType type;

    String code;

    String shortName;

    String color;

    Long externalId;

    String externalCode;

    CommonDTO parent;

    Integer sortOrder;

    CommonDTO company;
}
