package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.loghistory.LogHistoryListDTO;
import uz.uzkassa.smartpossupply.filters.ClickhouseFilter;
import uz.uzkassa.smartpossupply.service.ClickhouseLogHistoryService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.06.2023
 */
@RestController
@RequestMapping(ApiConstants.cabinetLogHistoryRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetLogHistoryResource {

    ClickhouseLogHistoryService clickhouseLogHistoryService;

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<LogHistoryListDTO>> getList(@ParameterObject ClickhouseFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(clickhouseLogHistoryService.getList(filter));
    }
}
