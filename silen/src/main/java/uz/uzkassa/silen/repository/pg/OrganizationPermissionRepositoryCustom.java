package uz.uzkassa.silen.repository.pg;

import org.springframework.data.repository.query.Param;
import uz.uzkassa.silen.dto.PermissionsCountDTO;

import java.util.Collection;
import java.util.List;

public interface OrganizationPermissionRepositoryCustom {

    List<PermissionsCountDTO> getSumOfByPermissionKeys(@Param("permissions") Collection<String> permissions, @Param("qty") int qty);

}
