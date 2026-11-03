package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.Fiscalization;
import uz.uzkassa.cts.dto.sync.fiscalization.FiscalizationSyncDTO;
import uz.uzkassa.cts.enums.RelationType;
import uz.uzkassa.cts.filter.BaseFilter;

public interface FiscalizationSyncService extends BaseService<Fiscalization, Long> {

    Page<FiscalizationSyncDTO> getList(BaseFilter filter);

    Long syncFiscal(Long id, RelationType relationType);

    Long syncTerminal(Long id, RelationType relationType);
}
