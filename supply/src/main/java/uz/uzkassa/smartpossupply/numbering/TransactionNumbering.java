package uz.uzkassa.smartpossupply.numbering;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.repository.TransactionRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TransactionNumbering extends AbstractNumbering {

    TransactionRepository transactionRepository;

    @Override
    public Prefix getPrefix() {
        return Prefix.TRANSACTION;
    }

    @Override
    protected Long getIncrementNumber(Long companyId) {
        return transactionRepository.getMaxIncrementNumber(companyId);
    }
}
