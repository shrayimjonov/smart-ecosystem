package uz.uzkassa.smartpossupply.dto.sms.smartpos;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SmartposSmsDTO implements Serializable {

    String originator;
    SmartposSmsContentDTO content;
}
