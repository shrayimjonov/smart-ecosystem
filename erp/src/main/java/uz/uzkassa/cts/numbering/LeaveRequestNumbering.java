package uz.uzkassa.cts.numbering;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.cts.enums.Prefix;
import uz.uzkassa.cts.repository.LeaveRequestRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LeaveRequestNumbering extends AbstractNumbering {

    LeaveRequestRepository leaveRequestRepository;

    @Override
    public Prefix getPrefix() {
        return Prefix.LEAVE_REQUEST;
    }

    @Override
    protected Long getIncrementNumber(Long companyId) {
        return leaveRequestRepository.getMaxIncrementNumber(companyId);
    }
}
