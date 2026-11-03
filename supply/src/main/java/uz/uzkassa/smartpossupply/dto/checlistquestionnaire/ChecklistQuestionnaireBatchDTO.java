package uz.uzkassa.smartpossupply.dto.checlistquestionnaire;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChecklistQuestionnaireBatchDTO implements Serializable {

    @NotNull
    Long checklistId;

    @NotNull
    @NotEmpty
    List<String> questions;
}
