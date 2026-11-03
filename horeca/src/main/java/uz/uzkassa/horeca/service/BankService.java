package uz.uzkassa.horeca.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.horeca.domain.Bank;
import uz.uzkassa.horeca.dto.bank.BankLookUpDTO;
import uz.uzkassa.horeca.filters.BaseFilter;
import uz.uzkassa.horeca.repository.BankRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class BankService extends BaseService {

    BankRepository bankRepository;

    public List<BankLookUpDTO> lookUp(BaseFilter filter) {
        if (filter.getId() != null) {
            Bank bank = bankRepository
                .findById(filter.getId())
                .orElseThrow(notFoundExceptionThrow(Bank.class.getSimpleName(), "id", filter.getId()));
            return List.of(bank.toLookUpDTO());
        }
        return bankRepository.getResultList(filter).getList().stream().map(Bank::toLookUpDTO).collect(Collectors.toList());
    }
}
