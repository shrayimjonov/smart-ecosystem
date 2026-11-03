package uz.uzkassa.smartpos.dto.base;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CommonCodeDTO extends CommonDTO {

    String code;

    public CommonCodeDTO(Long id, String name, String code) {
        super(id, name);
        this.code = code;
    }
}
