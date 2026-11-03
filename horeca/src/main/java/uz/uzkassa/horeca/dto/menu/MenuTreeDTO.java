package uz.uzkassa.horeca.dto.menu;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.horeca.dto.base.CommonDTO;
import uz.uzkassa.horeca.enums.CommonStatus;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MenuTreeDTO extends CommonDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4563446L;

    Long parentId;

    List<MenuTreeDTO> children = new ArrayList<>();

    CommonStatus status;

    @SuppressWarnings("unused")
    public MenuTreeDTO(Long id, String name, Long parentId, CommonStatus status) {
        this.id = id;
        this.name = name;
        this.parentId = parentId;
        this.status = status;
    }
}
