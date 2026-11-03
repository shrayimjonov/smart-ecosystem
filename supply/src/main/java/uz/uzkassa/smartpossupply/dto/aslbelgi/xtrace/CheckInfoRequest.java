package uz.uzkassa.smartpossupply.dto.aslbelgi.xtrace;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class CheckInfoRequest implements Serializable {

    List<String> codes;
}
