package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.hrms.VacancyApplicationTemplate;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.vacancyapplicationtemplate.VacancyApplicationTemplateDTO;
import uz.uzkassa.cts.dto.vacancyapplicationtemplate.admin.VacancyApplicationTemplateDetailAdminDTO;
import uz.uzkassa.cts.dto.vacancyapplicationtemplate.admin.VacancyApplicationTemplateListAdminDTO;
import uz.uzkassa.cts.filter.VacancyApplicationTemplateFilter;

import java.util.List;

public interface VacancyApplicationTemplateService extends BaseService<VacancyApplicationTemplate, Long> {

    Long create(VacancyApplicationTemplateDTO vacancyApplicationTemplateDTO);

    Long update(Long id, VacancyApplicationTemplateDTO vacancyApplicationTemplateDTO);

    VacancyApplicationTemplateDetailAdminDTO get(Long id);

    Page<VacancyApplicationTemplateListAdminDTO> getListForAdmin(VacancyApplicationTemplateFilter filter);

    List<CommonDTO> lookUp(VacancyApplicationTemplateFilter filter);
}
