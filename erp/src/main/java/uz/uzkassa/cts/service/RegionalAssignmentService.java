package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.regionalassignment.RegionalAssignment;
import uz.uzkassa.cts.dto.regionalassignment.RegionalAssignmentDTO;
import uz.uzkassa.cts.dto.regionalassignment.RegionalAssignmentDetailDTO;
import uz.uzkassa.cts.dto.regionalassignment.RegionalAssignmentListDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.RegionalAssignmentFilter;

public interface RegionalAssignmentService extends BaseService<RegionalAssignment, Long> {

    Long create(RegionalAssignmentDTO regionalAssignmentDTO);

    Long update(Long id, RegionalAssignmentDTO regionalAssignmentDTO);

    RegionalAssignmentDetailDTO get(Long id);

    Page<RegionalAssignmentListDTO> getList(RegionalAssignmentFilter filter);

    void updateStatus(Long id, CommonStatus status);
}
