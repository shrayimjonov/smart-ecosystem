package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.constants.MessageConstants;
import uz.uzkassa.cts.dto.uzcard.UzcardConnectionDTO;
import uz.uzkassa.cts.dto.uzcard.UzcardConnectionPayloadDTO;
import uz.uzkassa.cts.dto.uzcard.admin.UzcardConnectionListAdminDTO;
import uz.uzkassa.cts.filter.BaseFilter;

public interface UzcardConnectionService extends MessageConstants {

    void create(UzcardConnectionDTO uzcardConnectionDTO);

    void updateStatus(UzcardConnectionPayloadDTO payloadDTO);

    Page<UzcardConnectionListAdminDTO> getListForAdmin(BaseFilter filter);
}
