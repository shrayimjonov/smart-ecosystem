package uz.uzkassa.smartpossupply.numbering;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.repository.FinancialOperationRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class FinancialOperationNumbering extends AbstractNumbering {

    FinancialOperationRepository financialOperationRepository;

    @Override
    public Prefix getPrefix() {
        return Prefix.FINANCIAL_OPERATION;
    }

    @Override
    protected Long getIncrementNumber(Long companyId) {
        return financialOperationRepository.getMaxIncrementNumber(companyId);
    }
}
