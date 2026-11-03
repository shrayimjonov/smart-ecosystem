package uz.uzkassa.smartpossupply.web.rest.cabinet;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.smartpossupply.constants.ApiConstants;
import uz.uzkassa.smartpossupply.domain.aggregation.Aggregation;
import uz.uzkassa.smartpossupply.dto.aggregation.AggregationStatisticsDTO;
import uz.uzkassa.smartpossupply.dto.aggregationorder.AggregationOrderDTO;
import uz.uzkassa.smartpossupply.dto.aggregationorder.AggregationOrderDetailDTO;
import uz.uzkassa.smartpossupply.dto.aggregationorder.AggregationOrderListDTO;
import uz.uzkassa.smartpossupply.enums.AggregationOrderStatus;
import uz.uzkassa.smartpossupply.enums.PackageType;
import uz.uzkassa.smartpossupply.filters.AggregationOrderFilter;
import uz.uzkassa.smartpossupply.filters.BaseFilter;
import uz.uzkassa.smartpossupply.service.AggregationOrderService;

import java.util.EnumSet;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.cabinetAggregationOrderRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class CabinetAggregationOrderResource {

    AggregationOrderService aggregationOrderService;

    @Operation(summary = ApiConstants.Create)
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody AggregationOrderDTO aggregationOrderDTO) {
        aggregationOrderService.create(aggregationOrderDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = ApiConstants.List)
    @GetMapping
    public ResponseEntity<Page<AggregationOrderListDTO>> getList(@ParameterObject AggregationOrderFilter filter) {
        return ResponseEntity.ok(aggregationOrderService.getList(filter));
    }

    @Operation(summary = ApiConstants.Get)
    @GetMapping(ApiConstants.id)
    public ResponseEntity<AggregationOrderListDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(aggregationOrderService.get(id));
    }

    @GetMapping(ApiConstants.packageTypes)
    public ResponseEntity<EnumSet<PackageType>> getPackageTypes() {
        return ResponseEntity.ok(EnumSet.of(PackageType.BOX, PackageType.PALLET));
    }

    @GetMapping(ApiConstants.statuses)
    public ResponseEntity<EnumSet<AggregationOrderStatus>> getStatuses() {
        return ResponseEntity.ok(EnumSet.allOf(AggregationOrderStatus.class));
    }

    @GetMapping(ApiConstants.statistics)
    public ResponseEntity<AggregationStatisticsDTO> getStatistics(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(aggregationOrderService.getStatistics(filter));
    }
}
