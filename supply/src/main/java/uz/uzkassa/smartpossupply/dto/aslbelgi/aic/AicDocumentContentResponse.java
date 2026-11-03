package uz.uzkassa.smartpossupply.dto.aslbelgi.aic;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AicDocumentContentResponse implements Serializable {

    /**
     * Идентификатор текущего документа
     * в ИС МПТ
     * <p>
     * String (UUID)
     */
    String content;
}
