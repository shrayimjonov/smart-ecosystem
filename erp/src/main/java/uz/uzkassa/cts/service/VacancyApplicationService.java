package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.hrms.VacancyApplication;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.vacancyapplication.VacancyApplicationDTO;
import uz.uzkassa.cts.dto.vacancyapplication.admin.VacancyApplicationDetailAdminDTO;
import uz.uzkassa.cts.dto.vacancyapplication.admin.VacancyApplicationListAdminDTO;
import uz.uzkassa.cts.enums.VacancyApplicationStatus;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface VacancyApplicationService extends BaseService<VacancyApplication, Long> {

    Long create(VacancyApplicationDTO vacancyApplicationDTO);

    Long update(Long id, VacancyApplicationDTO vacancyApplicationDTO);

    VacancyApplicationDetailAdminDTO get(Long id);

    Page<VacancyApplicationListAdminDTO> getListForAdmin(BaseFilter filter);

    List<CommonDTO> lookUp(BaseFilter filter);

    void updateStatus(Long id, VacancyApplicationStatus status, String reason);

    Long convert(Long id);

    void uploadFile(Long id, MultipartFile file);
}
