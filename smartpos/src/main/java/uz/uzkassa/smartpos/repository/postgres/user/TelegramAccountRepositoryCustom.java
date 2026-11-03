package uz.uzkassa.smartpos.repository.postgres.user;


import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;
import uz.uzkassa.smartpos.domain.postgres.user.TelegramAccount;
import uz.uzkassa.smartpos.dto.filter.TelegramAccountFilter;


@Repository
public interface TelegramAccountRepositoryCustom {
    Page<TelegramAccount> findAllByFilter(TelegramAccountFilter filter);
}
