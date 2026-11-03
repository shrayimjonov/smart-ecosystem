package uz.uzkassa.smartpossupply.dto.address.yandex;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class YandexLocationNameRequest {

    @NonNull
    Double longitude;

    @NonNull
    Double latitude;

    String language;

    public YandexLocationNameRequest(Double longitude,Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
}
