package uz.uzkassa.smartpossupply.numbering;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.repository.PartyRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PartyNumbering extends AbstractNumbering {

    PartyRepository partyRepository;

    @Override
    public Prefix getPrefix() {
        return Prefix.PARTY;
    }

    @Override
    protected Long getIncrementNumber(Long companyId) {
        return partyRepository.getMaxIncrementNumber(companyId);
    }
}
