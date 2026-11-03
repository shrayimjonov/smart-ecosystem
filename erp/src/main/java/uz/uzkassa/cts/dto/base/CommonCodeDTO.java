package uz.uzkassa.cts.dto.base;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CommonCodeDTO extends CommonDTO {

    String code;

    public CommonCodeDTO(Long id, String name, String code) {
        super(id, name);
        this.code = code;
    }

    public CommonCodeDTO(String name, String code) {
        super(null, name);
        this.code = code;
    }
}
