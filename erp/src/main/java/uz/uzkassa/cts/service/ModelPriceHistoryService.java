package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import uz.uzkassa.cts.domain.ModelPriceHistory;
import uz.uzkassa.cts.dto.modelpricehistory.ModelPriceHistoryDetailDTO;
import uz.uzkassa.cts.dto.modelpricehistory.request.ModelPriceHistoryDto;
import uz.uzkassa.cts.dto.modelpricehistory.response.ModelPriceHistoryResponseDto;
import uz.uzkassa.cts.filter.ModelPriceHistoryFilter;

public interface ModelPriceHistoryService extends BaseService<ModelPriceHistory, Long> {
    ModelPriceHistoryResponseDto update(ModelPriceHistoryDto modelPriceHistoryDto);

    ModelPriceHistoryResponseDto create(ModelPriceHistoryDto modelPriceHistoryDto);

    ModelPriceHistoryResponseDto getById(Long id);

    Page<ModelPriceHistoryResponseDto> list(ModelPriceHistoryFilter filter);

    void customDeleteById(Long id);

    ModelPriceHistoryDetailDTO getDetail();
}
