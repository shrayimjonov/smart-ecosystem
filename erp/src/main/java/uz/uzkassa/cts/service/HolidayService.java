package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.Holiday;
import uz.uzkassa.cts.dto.holiday.HolidayDTO;
import uz.uzkassa.cts.dto.holiday.HolidayDetailDTO;
import uz.uzkassa.cts.dto.holiday.HolidayListDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;

public interface HolidayService extends BaseService<Holiday, Long> {

    Long create(HolidayDTO holidayDTO);

    Long update(Long id, HolidayDTO holidayDTO);

    HolidayDetailDTO get(Long id);

    Page<HolidayListDTO> getList(BaseFilter filter);

    void updateStatus(Long id, CommonStatus status);

    void createRecurring();
}
