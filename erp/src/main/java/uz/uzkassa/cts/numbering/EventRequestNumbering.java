package uz.uzkassa.cts.numbering;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.enums.Prefix;
import uz.uzkassa.cts.repository.EventRequestRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class EventRequestNumbering extends AbstractNumbering {

    EventRequestRepository eventRequestRepository;

    @Override
    public Prefix getPrefix() {
        return Prefix.EVENT_REQUEST;
    }

    @Override
    protected Long getIncrementNumber(Long companyId) {
        return eventRequestRepository.getMaxIncrementNumber(companyId);
    }
}
