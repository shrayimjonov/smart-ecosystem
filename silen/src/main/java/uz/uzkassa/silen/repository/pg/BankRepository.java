package uz.uzkassa.silen.repository.pg;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.uzkassa.silen.domain.Bank;

import java.util.List;

@Repository
public interface BankRepository extends JpaRepository<Bank, String> {
    List<Bank> findAllByOrderByNameRuAsc();
}
