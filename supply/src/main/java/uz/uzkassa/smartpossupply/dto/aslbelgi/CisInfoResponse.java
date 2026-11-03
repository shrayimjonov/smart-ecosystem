package uz.uzkassa.smartpossupply.dto.aslbelgi;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class CisInfoResponse implements Serializable {

    CisInfo cisInfo;
}
