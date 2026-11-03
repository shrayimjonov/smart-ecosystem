package uz.uzkassa.horeca.dto.sms;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class SmsRequestDTO implements Serializable {

    String phone;

    String code;
}
