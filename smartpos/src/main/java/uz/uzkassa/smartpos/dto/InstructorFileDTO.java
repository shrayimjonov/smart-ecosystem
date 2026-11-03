package uz.uzkassa.smartpos.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class InstructorFileDTO extends BaseAuditDTO {
    Long id;
    String description;
    List<Links> links;
    InstructorTypeDTO type;
    String parentCode;
    CommonFileDTO file;
    CommonFileDTO image;
}
