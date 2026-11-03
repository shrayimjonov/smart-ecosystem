package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.RentDeviceAct;
import uz.uzkassa.cts.dto.actoftransmission.response.ActChangDocFlowRequestDto;
import uz.uzkassa.cts.dto.rentDeviceAct.RentDeviceActDTO;
import uz.uzkassa.cts.dto.rentDeviceAct.RentDeviceActListDTO;
import uz.uzkassa.cts.dto.rentDeviceAct.RentDeviceActPayload;
import uz.uzkassa.cts.dto.rentDeviceAct.RentDeviceActPowerOfAttorneyDTO;
import uz.uzkassa.cts.filter.RentActFilter;

public interface RentDeviceActService extends BaseService<RentDeviceAct, Long> {

    Page<RentDeviceActListDTO> getList(RentActFilter filter);

    void create(RentDeviceActPayload dto);

    RentDeviceActDTO getById(Long id);

    String getHtmlTemplate(Long id);

    RentDeviceActDTO uploadFile(Long id, MultipartFile file);

    RentDeviceActDTO accept(Long id);

    RentDeviceActDTO reject(Long id);

    RentDeviceActDTO changeToAttach(Long id);

    RentDeviceActDTO changeDocFlowStatus(ActChangDocFlowRequestDto dto);

    String generatePowerOfAttorneyTemplate(RentDeviceActPowerOfAttorneyDTO dto);

    RentDeviceActDTO uploadPowerAttorneyFile(Long id, MultipartFile file);
}
