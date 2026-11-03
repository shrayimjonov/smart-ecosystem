package uz.uzkassa.smartpossupply.dto.aslbelgi.aic.xtrace;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AicDocumentXtraceRequest implements Serializable {

    /**
     * Тело операции, закодированное в
     * формате Base64. Формат тела = JSON
     * Перед кодированием properties JSON
     * должны быть отсортированы по
     * алфавиту (A-Z)
     */
    @NotNull
    String documentBody;

    /**
     * Открепленная подпись тела документа
     * (documentBody)
     */
    @NotNull
    String signature;
}
