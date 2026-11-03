package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.group.GroupDTO;

@Component
public class GroupValidator extends BaseValidator implements ValidatorStrategy<GroupDTO> {

    public GroupValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(GroupDTO dto) {
        validateRequiredField(dto.getName(), NAME);
        validateRequiredField(dto.getDepartmentId(), DEPARTMENT);
    }
}
