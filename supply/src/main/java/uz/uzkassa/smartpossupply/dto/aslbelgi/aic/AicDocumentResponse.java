package uz.uzkassa.smartpossupply.dto.aslbelgi.aic;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class AicDocumentResponse implements Serializable {

    /**
     * Идентификатор текущего документа
     * в ИС МПТ
     * <p>
     * String (UUID)
     */
    String documentId;
}
