package uz.uzkassa.smartpossupply.repository;

import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpossupply.domain.Account;

@Repository
public interface AccountRepository extends BaseRepository<Account, Long> {
}
