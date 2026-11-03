package uz.uzkassa.cts.dto.template;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.cts.enums.ThymeleafTemplateType;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class ThymeleafDTO implements Serializable {

    Long customerId;

    Long companyId;

    ThymeleafTemplateType templateType;
}
