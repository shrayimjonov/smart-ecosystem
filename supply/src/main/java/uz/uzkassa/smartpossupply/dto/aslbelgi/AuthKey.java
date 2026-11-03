package uz.uzkassa.smartpossupply.dto.aslbelgi;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthKey implements Serializable {

    String uuid;
    String data;
}
