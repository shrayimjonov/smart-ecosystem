package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.BlackList;
import uz.uzkassa.cts.dto.blacklist.request.BlackListRequestDto;
import uz.uzkassa.cts.dto.blacklist.response.BlackListResponseDto;
import uz.uzkassa.cts.filter.BaseFilter;

public interface BlackListService extends  BaseService<BlackList, Long>{
    Page<BlackListResponseDto> list(BaseFilter filter);

    BlackListResponseDto add(BlackListRequestDto dto);

    BlackListResponseDto getById(Long id);

    BlackListResponseDto updateById(Long id, BlackListRequestDto dto);
}
