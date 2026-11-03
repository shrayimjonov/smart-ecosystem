package uz.uzkassa.smartpossupply.dto.payload;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.TemplateType;
import uz.uzkassa.smartpossupply.enums.TemplateTypeAlias;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPayload implements Serializable {

    @Serial
    private static final long serialVersionUID = 144118198163515151L;

    TemplateType templateType;

    Long companyId;

    Map<TemplateTypeAlias, Object> params;
}
