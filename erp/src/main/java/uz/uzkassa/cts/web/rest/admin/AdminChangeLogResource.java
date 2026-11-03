package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.changelog.ChangeLogListDTO;
import uz.uzkassa.cts.filter.ClickhouseFilter;
import uz.uzkassa.cts.service.ChangeLogService;

import static lombok.AccessLevel.PRIVATE;


@RestController
@RequestMapping(ApiConstants.adminChangeLogRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminChangeLogResource {

    ChangeLogService changeLogService;

    @GetMapping
    public ResponseEntity<Page<ChangeLogListDTO>> getList(@ParameterObject ClickhouseFilter filter) {
        return ResponseEntity.ok(changeLogService.getList(filter));
    }
}
