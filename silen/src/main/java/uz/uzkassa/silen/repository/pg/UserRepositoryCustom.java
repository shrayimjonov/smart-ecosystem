package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.User;
import uz.uzkassa.silen.dto.filter.UserFilter;

public interface UserRepositoryCustom {
    Page<User> findAllByFilter(UserFilter filter);
}
