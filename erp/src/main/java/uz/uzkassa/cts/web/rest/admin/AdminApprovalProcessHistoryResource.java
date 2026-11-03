package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.approvalprocess.ApprovalProcessHistoryListAdminDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.service.ApprovalProcessHistoryService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(ApiConstants.adminApprovalProcessHistoryRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminApprovalProcessHistoryResource {

    ApprovalProcessHistoryService approvalProcessHistoryService;

    @GetMapping
    public ResponseEntity<List<ApprovalProcessHistoryListAdminDTO>> getList(Long entityId, EntityTypeEnum entityType) {
        return ResponseEntity.ok(approvalProcessHistoryService.getList(entityId, entityType));
    }

}
