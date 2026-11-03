package uz.uzkassa.cts.validator;

import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.approvalprocess.ApprovalProcessApproverDTO;
import uz.uzkassa.cts.dto.approvalprocess.ApprovalProcessDTO;
import uz.uzkassa.cts.dto.approvalprocess.ApprovalProcessStepDTO;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ApprovalProcessValidator extends BaseValidator implements ValidatorStrategy<ApprovalProcessDTO> {

    public ApprovalProcessValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(ApprovalProcessDTO dto) {
        validateRequiredField(dto.getEntityType(), ENTITY_TYPE);
        validateRequiredField(dto.getCompanyId(), COMPANY);
        validateRequiredField(dto.getSteps(), STEPS);
        for (ApprovalProcessStepDTO approvalProcessStepDTO : dto.getSteps()) {
            validateRequiredField(approvalProcessStepDTO.getStep(), STEPS);
            validateRequiredField(approvalProcessStepDTO.getApproveAction(), APPROVER);
            validateRequiredField(approvalProcessStepDTO.getRejectAction(), REJECT);
            validateRequiredField(approvalProcessStepDTO.getApprovers(), APPROVER);
            for (ApprovalProcessApproverDTO approvalProcessApproverDTO : approvalProcessStepDTO.getApprovers()) {
                if (approvalProcessApproverDTO.getEmployeeId() == null && approvalProcessApproverDTO.getRole() == null) {
                    throw new BadRequestException(localizationMessage.localize(REQUIRED, new Object[]{localizationMessage.localize(EMPLOYEE)}));
                }
                validateRequiredField(approvalProcessApproverDTO.getType(), TYPE);
            }
        }
    }
}
