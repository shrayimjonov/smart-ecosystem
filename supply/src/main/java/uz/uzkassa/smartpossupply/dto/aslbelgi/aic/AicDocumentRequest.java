package uz.uzkassa.smartpossupply.dto.aslbelgi.aic;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@ToString
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AicDocumentRequest implements Serializable {

    /**
     * Содержимое обогащенного
     * документа JSON в кодировке base64.
     * Структура и формат уведомления
     * приведена ниже
     * <p>
     * String, Base64
     */
    @NotNull
    String document;

    /**
     * Открепленная ЭЦП в base64
     * <p>
     * String, Base64
     */
    @NotNull
    String signature;
}
