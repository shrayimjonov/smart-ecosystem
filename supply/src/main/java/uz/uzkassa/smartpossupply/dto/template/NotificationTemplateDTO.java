package uz.uzkassa.smartpossupply.dto.template;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.enums.ApplicationType;
import uz.uzkassa.smartpossupply.enums.TemplateType;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.02.2023 12:28
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationTemplateDTO {

    @NotBlank
    String subjectUz;

    @NotBlank
    String subjectRu;

    @NotBlank
    String subjectCyrillic;

    @NotBlank
    String descriptionUz;

    @NotBlank
    String descriptionRu;

    @NotBlank
    String descriptionCyrillic;

    @NotNull
    TemplateType templateType;

    @NotNull
    ApplicationType applicationType;
}
