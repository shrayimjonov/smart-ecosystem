package uz.uzkassa.smartpos.filter;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.smartpos.dto.filter.PrimaryFilter;
import uz.uzkassa.smartpos.enums.InstructorType;

import java.util.List;

@Getter
@Setter
public class InstructorFilter extends PrimaryFilter {
    private List<String> parentCodes;
    private List<InstructorType> types;
}
