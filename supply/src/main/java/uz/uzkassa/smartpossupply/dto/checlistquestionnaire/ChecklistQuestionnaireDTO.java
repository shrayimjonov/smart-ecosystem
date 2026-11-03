package uz.uzkassa.smartpossupply.dto.checlistquestionnaire;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChecklistQuestionnaireDTO implements Serializable {

    Long id;

    @NotNull
    Long checklistId;

    @NotNull
    String question;
}
