package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.hrms.Groups;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.group.GroupDTO;
import uz.uzkassa.cts.dto.group.admin.GroupDetailAdminDTO;
import uz.uzkassa.cts.dto.group.admin.GroupListAdminDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface GroupService extends BaseService<Groups, Long> {

    Long create(GroupDTO groupDTO);

    Long update(Long id, GroupDTO groupDTO);

    Page<GroupListAdminDTO> getListForAdmin(BaseFilter filter);

    GroupDetailAdminDTO getForAdmin(Long id);

    List<CommonDTO> lookUp(BaseFilter filter);

    void updateStatus(Long id, CommonStatus status);
}
