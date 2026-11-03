package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.AccessoryModel;
import uz.uzkassa.cts.dto.accessorymodel.AccessoryModelDTO;
import uz.uzkassa.cts.dto.accessorymodel.AccessoryModelDetailDTO;
import uz.uzkassa.cts.dto.accessorymodel.AccessoryModelListDTO;
import uz.uzkassa.cts.dto.accessorymodel.request.AccessoryModelCreateRequestDto;
import uz.uzkassa.cts.dto.accessorymodel.request.AccessoryModelUpdateRequestDto;
import uz.uzkassa.cts.dto.accessorymodel.response.AccessoryModelResponseDto;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.filter.BaseFilter;

import java.util.List;


public interface AccessoryModelService extends BaseService<AccessoryModel, Long> {
    Page<AccessoryModelResponseDto> list(BaseFilter filter);

    AccessoryModelResponseDto getById(Long id);

    AccessoryModelResponseDto create(AccessoryModelCreateRequestDto dto);

    AccessoryModelResponseDto update(AccessoryModelUpdateRequestDto dto);

    Long create(AccessoryModelDTO accessoryModelDTO);

    Long update(Long id, AccessoryModelDTO accessoryModelDTO);

    AccessoryModelDetailDTO get(Long id);

    Page<AccessoryModelListDTO> getList(BaseFilter filter);

    List<CommonDTO> lookUp(BaseFilter filter);

    void updateStatus(Long id, CommonStatus status);
}
