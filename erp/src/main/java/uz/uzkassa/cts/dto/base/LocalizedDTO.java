package uz.uzkassa.cts.dto.base;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED)
public class LocalizedDTO implements Serializable {

    Long id;

    String nameUzLatin;

    String nameUzCyrillic;

    String nameRu;
}
