package uz.uzkassa.cts.dto.base;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class CommonStringDTO implements Serializable {

    String id;

    String name;
}
