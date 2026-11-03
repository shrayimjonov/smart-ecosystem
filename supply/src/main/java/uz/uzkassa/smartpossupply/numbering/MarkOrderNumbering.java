package uz.uzkassa.smartpossupply.numbering;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.repository.MarkOrderRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MarkOrderNumbering extends AbstractNumbering {

    MarkOrderRepository markOrderRepository;

    @Override
    public Prefix getPrefix() {
        return Prefix.MARK_ORDER;
    }

    @Override
    protected Long getIncrementNumber(Long companyId) {
        return markOrderRepository.getMaxIncrementNumber(companyId);
    }
}
