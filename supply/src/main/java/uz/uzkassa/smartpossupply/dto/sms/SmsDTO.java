package uz.uzkassa.smartpossupply.dto.sms;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SmsDTO implements Serializable {

    String phone;

    String message;

    public SmsDTO(String phone) {
        this.phone = phone;
    }

    public SmsDTO(String phone, String message) {
        this.phone = phone;
        this.message = message;
    }
}
