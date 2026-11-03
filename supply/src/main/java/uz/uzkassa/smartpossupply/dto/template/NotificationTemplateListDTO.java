package uz.uzkassa.smartpossupply.dto.template;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.ApplicationType;
import uz.uzkassa.smartpossupply.enums.SendType;
import uz.uzkassa.smartpossupply.enums.TemplateType;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.02.2023 14:14
 */
@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationTemplateListDTO {

    Long id;

    String subjectUz;

    String subjectRu;

    String subjectCyrillic;

    ApplicationType applicationType;

    TemplateType templateType;
}
