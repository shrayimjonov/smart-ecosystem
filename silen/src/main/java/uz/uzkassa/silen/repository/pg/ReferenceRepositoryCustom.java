package uz.uzkassa.silen.repository.pg;

import org.springframework.data.domain.Page;
import uz.uzkassa.silen.domain.Reference;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.filter.ReferenceFilter;

import java.util.List;

public interface ReferenceRepositoryCustom {
    Page<Reference> findAllByFilter(ReferenceFilter filter);

    List<SelectItem> getItems(ReferenceFilter filter);
}
