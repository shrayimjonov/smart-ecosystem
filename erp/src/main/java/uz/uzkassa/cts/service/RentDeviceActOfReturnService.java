package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.RentDeviceActOfReturn;
import uz.uzkassa.cts.dto.actoftransmission.response.ActChangeDocFlowRequestDto;
import uz.uzkassa.cts.dto.rentDeviceAct.RentDeviceActOfReturnDTO;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;

public interface RentDeviceActOfReturnService extends BaseService<RentDeviceActOfReturn, Long> {
    List<RentDeviceActOfReturnDTO> getByCustomerId(Long id);

    Page<RentDeviceActOfReturnDTO> getList(BaseFilter filter);

    RentDeviceActOfReturnDTO create(Long rentDeviceActId);

    RentDeviceActOfReturnDTO getById(Long id);

    String getHtmlTemplate(Long id);

    RentDeviceActOfReturnDTO uploadFile(Long id, MultipartFile file);

    RentDeviceActOfReturnDTO accept(Long id);

    RentDeviceActOfReturnDTO changeToAttach(Long id);

    RentDeviceActOfReturnDTO changeDocFlowStatus(ActChangeDocFlowRequestDto dto);

    RentDeviceActOfReturnDTO findByCustomerIdAndDeviceSerialNumber(Long customerId, String deviceSerialNumber);
}
