package uz.uzkassa.smartpossupply.numbering;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.repository.FacturaRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FacturaNumbering extends AbstractNumbering {

    FacturaRepository facturaRepository;

    @Override
    public Prefix getPrefix() {
        return Prefix.INVOICE;
    }

    @Override
    protected Long getIncrementNumber(Long companyId) {
        return facturaRepository.getMaxIncrementNumber(companyId);
    }
}
