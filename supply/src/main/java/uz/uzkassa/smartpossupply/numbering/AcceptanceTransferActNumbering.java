package uz.uzkassa.smartpossupply.numbering;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.repository.AcceptanceTransferActRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AcceptanceTransferActNumbering extends AbstractNumbering {

    AcceptanceTransferActRepository acceptanceTransferActRepository;

    @Override
    public Prefix getPrefix() {
        return Prefix.ACCEPTANCE_TRANSFER_ACT;
    }

    @Override
    protected Long getIncrementNumber(Long companyId) {
        return acceptanceTransferActRepository.getMaxIncrementNumber(companyId);
    }
}
