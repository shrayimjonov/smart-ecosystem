package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.Integration;
import uz.uzkassa.cts.dto.integration.admin.IntegrationDTO;
import uz.uzkassa.cts.dto.integration.admin.IntegrationDetailAdminDTO;
import uz.uzkassa.cts.dto.integration.admin.IntegrationListAdminDTO;
import uz.uzkassa.cts.filter.BaseFilter;

public interface IntegrationService extends BaseService<Integration, Long> {

    Long create(IntegrationDTO integrationDTO);

    Long update(Long id, IntegrationDTO integrationDTO);

    IntegrationDetailAdminDTO get(Long id);

    Page<IntegrationListAdminDTO> getListForAdmin(BaseFilter filter);

    void uploadLogo(Long id, MultipartFile file);

    void check(Long id);
}
