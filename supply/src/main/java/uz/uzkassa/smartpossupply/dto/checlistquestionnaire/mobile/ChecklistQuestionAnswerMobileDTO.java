package uz.uzkassa.smartpossupply.dto.checlistquestionnaire.mobile;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PROTECTED)
public class ChecklistQuestionAnswerMobileDTO implements Serializable {

    @NotNull
    String answer;
}
