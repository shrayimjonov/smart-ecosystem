package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.timeslot.Timeslot;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.timeslot.TimeslotDTO;
import uz.uzkassa.cts.dto.timeslot.admin.TimeslotListAdminDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.TimeslotFilter;

import java.util.List;

public interface TimeslotService extends BaseService<Timeslot, Long> {
    Long create(TimeslotDTO timeslotDTO);

    Long update(Long id, TimeslotDTO timeslotDTO);

    TimeslotDTO getRoundTheClock();

    TimeslotDTO get(Long id);

    TimeslotDTO getDetail(Timeslot timeslot);

    TimeslotDTO getByEntityIdAndEntityType(Long entityId, EntityTypeEnum entityType);

    List<CommonDTO> getItems();

    void updateStatus(Long id, CommonStatus status);

    Page<TimeslotListAdminDTO> getListForAdmin(TimeslotFilter filter);

    List<CommonDTO> lookUp(TimeslotFilter filter);
}
