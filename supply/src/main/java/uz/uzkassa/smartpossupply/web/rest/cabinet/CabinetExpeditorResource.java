package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.dto.expeditor.ExpeditorDetailDTO;
import uz.uzkassa.smartpossupply.dto.expeditor.ExpeditorListDTO;
import uz.uzkassa.smartpossupply.dto.expeditor.ExpeditorStatisticsDTO;
import uz.uzkassa.smartpossupply.filters.ExpeditorFilter;
import uz.uzkassa.smartpossupply.service.ExpeditorService;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.ArrayList;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetExpeditorRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetExpeditorResource {

    ExpeditorService expeditorService;

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<ExpeditorDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(expeditorService.get(id));
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<ExpeditorListDTO>> getList(@ParameterObject ExpeditorFilter filter) {
        if (SecurityUtils.getCurrentCompanyId() == null) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), filter.getPageable(), 0L));
        }
        return ResponseEntity.ok(expeditorService.getList(filter));
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<ExpeditorStatisticsDTO> getStatistics(@ParameterObject ExpeditorFilter filter) {
        return ResponseEntity.ok(expeditorService.getStatistics(filter));
    }
}
