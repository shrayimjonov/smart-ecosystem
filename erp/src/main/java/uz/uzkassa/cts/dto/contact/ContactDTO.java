package uz.uzkassa.cts.dto.contact;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.dto.base.CommonDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContactDTO extends CommonDTO implements Serializable {

    Long typeId;

    boolean isPrimary;

    public ContactDTO(Long id, String name, Long typeId, boolean isPrimary) {
        super(id, name);
        this.typeId = typeId;
        this.isPrimary = isPrimary;
    }

    public ContactDTO() {
    }
}
