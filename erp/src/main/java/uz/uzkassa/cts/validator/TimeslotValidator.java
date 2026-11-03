package uz.uzkassa.cts.validator;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.dto.timeslot.TimeslotDTO;
import uz.uzkassa.cts.enums.TimeslotType;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TimeslotValidator extends BaseValidator implements ValidatorStrategy<TimeslotDTO> {

    public TimeslotValidator(LocalizationMessage localizationMessage) {
        super(localizationMessage);
    }

    public void validate(TimeslotDTO dto) {
        validateRequiredField(dto.getType(), TYPE);
        if (TimeslotType.STANDARD == dto.getType()) {
            validateRequiredField(dto.getItems(), TIMESLOT_ITEM);
            if (dto.getItems().size() != 7) {
                throw new BadRequestException(localizationMessage.localize(REQUIRED, new Object[]{localizationMessage.localize(TIMESLOT_ITEM)}));
            }
            dto.getItems().forEach(timeslotItemDTO -> {
                validateRequiredField(timeslotItemDTO.getDay(), DAY);
            });
        } else {
            if (!(dto.getFromTime() != null && dto.getToTime() != null || dto.getWorkingHour() != null)) {
                throw new BadRequestException(localizationMessage.localize(REQUIRED, new Object[]{localizationMessage.localize(WORKING_HOURS)}));
            }
        }
    }
}
