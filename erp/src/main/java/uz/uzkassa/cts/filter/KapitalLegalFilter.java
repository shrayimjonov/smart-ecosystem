package uz.uzkassa.cts.filter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDirectionType;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDocumentState;
import uz.uzkassa.cts.enums.kapitalbank.KapitalLegalDocumentType;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class KapitalLegalFilter extends BaseFilter {
    KapitalLegalDirectionType directionType;
    KapitalLegalDocumentType documentType;
    KapitalLegalDocumentState state;
    Boolean sentToBilling;
}
