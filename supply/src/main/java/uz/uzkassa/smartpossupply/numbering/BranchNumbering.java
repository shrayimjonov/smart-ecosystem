package uz.uzkassa.smartpossupply.numbering;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.enums.BranchType;
import uz.uzkassa.smartpossupply.enums.Prefix;
import uz.uzkassa.smartpossupply.repository.BranchRepository;

import static lombok.AccessLevel.PRIVATE;

@Component
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class BranchNumbering extends AbstractNumbering {

    BranchRepository branchRepository;

    @Override
    public Prefix getPrefix() {
        return Prefix.WEB_KASSA;
    }

    @Override
    protected Long getIncrementNumber(Long companyId) {
        return branchRepository.getMaxIncrementNumber(companyId, BranchType.WEB_KASSA);
    }
}
