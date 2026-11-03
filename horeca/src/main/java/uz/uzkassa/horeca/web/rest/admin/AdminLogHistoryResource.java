package uz.uzkassa.horeca.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.horeca.constants.ApiConstants;
import uz.uzkassa.horeca.dto.loghistory.LogHistoryListDTO;
import uz.uzkassa.horeca.filters.ClickhouseFilter;
import uz.uzkassa.horeca.service.ClickhouseLogHistoryService;

import static lombok.AccessLevel.PRIVATE;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 16.06.2023
 */
@RestController
@RequestMapping(ApiConstants.adminLogHistoryRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminLogHistoryResource {

    ClickhouseLogHistoryService clickhouseLogHistoryService;

    @GetMapping
    public ResponseEntity<Page<LogHistoryListDTO>> getList(ClickhouseFilter filter) {
        return ResponseEntity.ok(clickhouseLogHistoryService.getList(filter));
    }
}
