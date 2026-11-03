package uz.uzkassa.smartpos.web.rest.fashion;

import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.dto.fashion.balance.FashionBalanceHistoryDto;
import uz.uzkassa.smartpos.dto.fashion.balance.FashionBalanceHistoryStatisticsDto;
import uz.uzkassa.smartpos.dto.fashion.balance.FashionColorGroupHistoryDto;
import uz.uzkassa.smartpos.filter.FashionBalanceHistoryFilter;
import uz.uzkassa.smartpos.service.fashion.FashionBalanceHistoryService;
import uz.uzkassa.smartpos.utils.ServerUtils;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fashion/cabinet/balance-history")
public class FashionCabinetBalanceHistoryResource {
    private final FashionBalanceHistoryService fashionBalanceHistoryService;

    @GetMapping
    public Page<FashionBalanceHistoryDto> findAllByFilter(@ParameterObject FashionBalanceHistoryFilter filter) {
        return fashionBalanceHistoryService.findAllByFilter(filter);
    }

    @GetMapping("/excel")
    public ResponseEntity<Resource> findAllByFilterExcel(@ParameterObject FashionBalanceHistoryFilter filter) {
        return ServerUtils.getResponseEntity(fashionBalanceHistoryService.findAllByFilterExcel(filter), "Материальный отчет");
    }

    @GetMapping("/statistics")
    public FashionBalanceHistoryStatisticsDto getStatisticsByFilter(@ParameterObject FashionBalanceHistoryFilter filter) {
        return fashionBalanceHistoryService.getStatisticsByFilter(filter);
    }

    @GetMapping("/color-group")
    public List<FashionColorGroupHistoryDto> findAllColorGroupByFilter(@ParameterObject FashionBalanceHistoryFilter filter) {
        return fashionBalanceHistoryService.findAllColorGroupByFilter(filter);
    }
}
