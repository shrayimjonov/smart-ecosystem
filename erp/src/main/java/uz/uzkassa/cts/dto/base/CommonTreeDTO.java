package uz.uzkassa.cts.dto.base;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class CommonTreeDTO extends CommonDTO {

    List<CommonTreeDTO> children = new ArrayList<>();

    public CommonTreeDTO(Long id, String name) {
        super(id, name);
    }
}
