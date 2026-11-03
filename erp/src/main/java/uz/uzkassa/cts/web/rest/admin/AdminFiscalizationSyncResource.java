package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.sync.fiscalization.FiscalizationSyncDTO;
import uz.uzkassa.cts.enums.RelationType;
import uz.uzkassa.cts.filter.CustomerFilter;
import uz.uzkassa.cts.service.FiscalizationSyncService;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminFiscalizationSyncRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminFiscalizationSyncResource {

    FiscalizationSyncService fiscalizationSyncService;

    @GetMapping
    public ResponseEntity<Page<FiscalizationSyncDTO>> getList(@ParameterObject CustomerFilter filter) {
        return ResponseEntity.ok(fiscalizationSyncService.getList(filter));
    }

    @GetMapping("/{id}/{relationType}")
    public ResponseEntity<Long> syncTo(@PathVariable Long id, @PathVariable RelationType relationType) {
        return ResponseEntity.ok(fiscalizationSyncService.syncFiscal(id, relationType));
    }
}
