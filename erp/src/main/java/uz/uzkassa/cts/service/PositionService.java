package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.hrms.Position;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.CommonTreeDTO;
import uz.uzkassa.cts.dto.position.PositionDTO;
import uz.uzkassa.cts.dto.position.admin.PositionDetailAdminDTO;
import uz.uzkassa.cts.dto.position.admin.PositionListAdminDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface PositionService extends BaseService<Position, Long> {

    Long create(PositionDTO positionDTO);

    Long update(Long id, PositionDTO positionDTO);

    List<PositionListAdminDTO> getListForAdmin(BaseFilter filter);

    PositionDetailAdminDTO getForAdmin(Long id);

    List<CommonDTO> getItems(Long companyId);

    void updateStatus(Long id, CommonStatus status);

    List<CommonTreeDTO> treeList(BaseFilter filter);
}
