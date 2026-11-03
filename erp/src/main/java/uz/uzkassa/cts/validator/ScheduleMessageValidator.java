package uz.uzkassa.cts.validator;

import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.schedulemessage.ScheduleMessageDTO;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

import java.time.LocalDateTime;

@Component
public class ScheduleMessageValidator extends BaseValidator implements ValidatorStrategy<ScheduleMessageDTO> {

    public ScheduleMessageValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    @Override
    public void validate(ScheduleMessageDTO dto) {
        validateRequiredField(dto.getSubject(), SUBJECT);
        validateRequiredField(dto.getMessage(), MESSAGE);
        validateRequiredField(dto.getScheduleDate(), SCHEDULED_DATE);
        validateRequiredField(dto.getChannelType(), CHANNEL_TYPE);
        validateRequiredField(dto.getMailingListId(), MAILING_LIST);
        if (dto.getScheduleDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(localizationMessage.localize(INVALID_INPUT, new Object[]{localizationMessage.localize(SCHEDULED_DATE)}));
        }
    }
}
