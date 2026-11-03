package uz.uzkassa.smartpossupply.numbering;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.enums.TransferType;
import uz.uzkassa.smartpossupply.repository.StockInRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class StockInNumbering extends AbstractNumbering {

    StockInRepository stockInRepository;

    @Override
    public Prefix getPrefix() {
        return Prefix.STOCK_IN;
    }

    @Override
    protected Long getIncrementNumber(Long companyId) {
        return stockInRepository.getMaxIncrementNumber(companyId, TransferType.IN);
    }
}
