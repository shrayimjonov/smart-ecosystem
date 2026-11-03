package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.Bank;
import uz.uzkassa.smartpossupply.filters.BankFilter;

import java.util.List;

@Repository
public interface BankRepositoryCustom {
    List<Bank> lookUp(BankFilter filter);
}
