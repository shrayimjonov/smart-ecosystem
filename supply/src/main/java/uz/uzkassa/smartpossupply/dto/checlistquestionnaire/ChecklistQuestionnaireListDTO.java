package uz.uzkassa.smartpossupply.dto.checlistquestionnaire;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChecklistQuestionnaireListDTO implements Serializable {

    Long id;

    String question;
}
