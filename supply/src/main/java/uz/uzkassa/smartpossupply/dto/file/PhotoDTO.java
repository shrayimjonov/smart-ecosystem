package uz.uzkassa.smartpossupply.dto.file;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 05.05.2023
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PhotoDTO {

    @NotNull
    String body;

    @NotNull
    String fileName;

    @NotNull
    String contentType;
}
