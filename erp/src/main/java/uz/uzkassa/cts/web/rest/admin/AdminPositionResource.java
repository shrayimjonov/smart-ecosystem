package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.constants.CacheConstants;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.CommonTreeDTO;
import uz.uzkassa.cts.dto.position.PositionDTO;
import uz.uzkassa.cts.dto.position.admin.PositionDetailAdminDTO;
import uz.uzkassa.cts.dto.position.admin.PositionListAdminDTO;
import uz.uzkassa.cts.enums.CommonStatus;
import uz.uzkassa.cts.enums.ContentType;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.service.ExportReportService;
import uz.uzkassa.cts.service.PositionService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminPositionRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminPositionResource {

    PositionService positionService;
    ExportReportService exportReportService;

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody PositionDTO positionDTO) {
        return ResponseEntity.ok(positionService.create(positionDTO));
    }

    @PutMapping(ApiConstants.id)
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody PositionDTO positionDTO) {
        return ResponseEntity.ok(positionService.update(id, positionDTO));
    }

    @GetMapping(ApiConstants.id)
    public ResponseEntity<PositionDetailAdminDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(positionService.getForAdmin(id));
    }

    @GetMapping
    @Cacheable(cacheNames = CacheConstants.POSITION_LIST_CACHE)
    public ResponseEntity<List<PositionListAdminDTO>> getList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(positionService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.tree)
    public ResponseEntity<List<CommonTreeDTO>> treeList(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(positionService.treeList(filter));
    }

    @GetMapping(ApiConstants.items)
    @Cacheable(cacheNames = CacheConstants.POSITION_ITEMS_CACHE)
    public ResponseEntity<List<CommonDTO>> getItems(@RequestParam(required = false) Long companyId) {
        return ResponseEntity.ok(positionService.getItems(companyId));
    }

    @PatchMapping(ApiConstants.updateStatus)
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam("status") CommonStatus status) {
        positionService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportExcel)
    @Cacheable(cacheNames = CacheConstants.POSITION_LIST_CACHE)
    public ResponseEntity<Void> exportToExcel(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.POSITION, ContentType.EXCEL);
        return ResponseEntity.ok().build();
    }

    @GetMapping(ApiConstants.exportCSV)
    @Cacheable(cacheNames = CacheConstants.POSITION_LIST_CACHE)
    public ResponseEntity<Void> exportToCSV(@ParameterObject BaseFilter filter) {
        exportReportService.export(filter, EntityTypeEnum.POSITION, ContentType.CSV);
        return ResponseEntity.ok().build();
    }
}
