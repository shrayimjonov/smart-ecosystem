package uz.uzkassa.smartpos.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpos.enums.InstructorType;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InstructorFileCreateDTO {
    Long fileId;
    Long imageId;
    List<Links> links;
    String description;
    InstructorType type;
}
