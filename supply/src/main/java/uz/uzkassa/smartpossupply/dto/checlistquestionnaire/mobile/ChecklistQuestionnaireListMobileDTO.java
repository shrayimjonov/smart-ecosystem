package uz.uzkassa.smartpossupply.dto.checlistquestionnaire.mobile;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChecklistQuestionnaireListMobileDTO extends ChecklistQuestionAnswerMobileDTO implements Serializable {

    Long id;

    String question;
}
