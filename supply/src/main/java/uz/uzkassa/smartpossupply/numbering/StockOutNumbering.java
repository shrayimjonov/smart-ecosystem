package uz.uzkassa.smartpossupply.numbering;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.repository.StockOutRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StockOutNumbering extends AbstractNumbering {

    StockOutRepository stockOutRepository;

    @Override
    public Prefix getPrefix() {
        return Prefix.STOCK_OUT;
    }

    @Override
    protected Long getIncrementNumber(Long companyId) {
        return stockOutRepository.getMaxIncrementNumber(companyId, TransferType.OUT);
    }
}
