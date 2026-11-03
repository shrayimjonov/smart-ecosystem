package uz.uzkassa.smartpossupply.dto.address.yandex;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class YandexLocationNameResponse {
    String name;
}
