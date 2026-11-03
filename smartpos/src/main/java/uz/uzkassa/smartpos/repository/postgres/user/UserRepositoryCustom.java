package uz.uzkassa.smartpos.repository.postgres.user;

import uz.uzkassa.smartpos.dto.ManagerLookUpDTO;
import uz.uzkassa.smartpos.dto.SelectItem;
import uz.uzkassa.smartpos.filter.specification.UserSpecificationFilter;

import java.util.List;

public interface UserRepositoryCustom {
    List<SelectItem> search(UserSpecificationFilter filter);

    List<ManagerLookUpDTO> searchManager(UserSpecificationFilter filter);

}
