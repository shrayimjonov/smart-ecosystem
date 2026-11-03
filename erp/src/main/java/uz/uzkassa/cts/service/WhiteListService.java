package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.WhiteList;
import uz.uzkassa.cts.dto.whitelist.WhiteListDetailDTO;
import uz.uzkassa.cts.dto.whitelist.WhiteListListDTO;
import uz.uzkassa.cts.dto.whitelist.WhiteListNewDTO;
import uz.uzkassa.cts.filter.WhiteListFilter;


public interface WhiteListService extends BaseService<WhiteList, Long> {

    Long create(WhiteListNewDTO whiteListNewDTO);

    Long update(Long id, WhiteListNewDTO whiteListNewDTO);

    WhiteListDetailDTO get(Long id);

    Page<WhiteListListDTO> getList(WhiteListFilter filter);

    void updateStatusCron();

}
