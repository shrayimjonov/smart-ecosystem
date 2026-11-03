package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.PrintTemplate;
import uz.uzkassa.silen.dto.filter.PrintTemplateFilter;

public interface PrintTemplateRepositoryCustom {

    Page<PrintTemplate> findAllByFilter(PrintTemplateFilter filter);
}
