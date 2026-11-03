package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.hrms.Vacancy;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.vacancy.VacancyDTO;
import uz.uzkassa.cts.dto.vacancy.VacancyDetailAdminDTO;
import uz.uzkassa.cts.dto.vacancy.VacancyListAdminDTO;
import uz.uzkassa.cts.enums.VacancyStatus;
import uz.uzkassa.cts.filter.VacancyFilter;

import java.util.List;

public interface VacancyService extends BaseService<Vacancy, Long> {

    Long create(VacancyDTO vacancyDTO);

    Long update(Long id, VacancyDTO vacancyDTO);

    VacancyDetailAdminDTO get(Long id);

    Page<VacancyListAdminDTO> getListForAdmin(VacancyFilter filter);

    List<CommonDTO> lookUp(VacancyFilter filter);

    void updateStatus(Long id, VacancyStatus status);

    void assignVacancyToHr(Long id, Long hrId);
}
