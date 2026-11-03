package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.Reference;
import uz.uzkassa.cts.dto.base.CommonCodeDTO;
import uz.uzkassa.cts.dto.reference.ReferenceDTO;
import uz.uzkassa.cts.dto.reference.admin.ReferenceDetailAdminDTO;
import uz.uzkassa.cts.dto.reference.admin.ReferenceListAdminDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.ReferenceType;
import uz.uzkassa.cts.filter.ReferenceFilter;

import java.util.List;

public interface ReferenceService extends BaseService<Reference, Long> {

    Long create(ReferenceDTO referenceDTO);

    Long update(Long id, ReferenceDTO referenceDTO);

    Page<ReferenceListAdminDTO> getListForAdmin(ReferenceFilter filter);

    List<CommonCodeDTO> lookUp(ReferenceFilter filter);

    ReferenceDetailAdminDTO get(Long id);

    void updateStatus(Long id, CommonStatus status);

    List<CommonCodeDTO> getItems(ReferenceType type);
}
