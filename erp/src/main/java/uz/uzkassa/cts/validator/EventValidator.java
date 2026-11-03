package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.cts.dto.event.EventDTO;

@Component
public class EventValidator extends BaseValidator implements ValidatorStrategy<EventDTO> {

    public EventValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(EventDTO dto) {
        validateRequiredField(dto.getSubject(), SUBJECT);
        validateRequiredField(dto.getCompanyId(), COMPANY);
        validateRequiredField(dto.getFromDate(), DATE);
        validateRequiredField(dto.getToDate(), DATE);
        validateRequiredField(dto.getParticipantIds(), PARTICIPANTS);
        if (!CollectionUtils.isEmpty(dto.getReminders())) {
            dto.getReminders().forEach(reminderDTO -> {
                validateRequiredField(reminderDTO.getCode(), CODE);
                validateRequiredField(reminderDTO.getType(), TYPE);
            });
        }
    }
}
