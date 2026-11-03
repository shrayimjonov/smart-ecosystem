package uz.uzkassa.smartpossupply.dto.aslbelgi;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 07.12.2022 12:16
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AslBelgiTokenDTO {

    String tin;
    String token;
}
