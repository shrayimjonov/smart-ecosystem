package uz.uzkassa.cts.web.rest.admin;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.constants.ApiConstants;
import uz.uzkassa.cts.dto.chat.ChatDTO;
import uz.uzkassa.cts.dto.chat.admin.ChatListAdminDTO;
import uz.uzkassa.cts.dto.employee.EmployeeListLoginDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.EmployeeFilter;
import uz.uzkassa.cts.service.ChatService;

import java.util.List;

import static lombok.AccessLevel.PRIVATE;

/**
 * Created date time: 13.02.2025 16:17
 */

@RestController
@RequestMapping(ApiConstants.adminChatRootApi)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdminChatController {

    ChatService chatService;

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody ChatDTO chatDTO) {
        chatService.create(chatDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = ApiConstants.upload, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> upload(@RequestPart MultipartFile file,
                                       @RequestParam Long entityId,
                                       @RequestParam EntityTypeEnum entityType,
                                       @RequestParam(required = false) String message,
                                       @RequestParam(required = false) List<Long> memberIds) {
        chatService.upload(file, entityId, entityType, message, memberIds);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Page<ChatListAdminDTO>> getListForAdmin(@ParameterObject BaseFilter filter) {
        return ResponseEntity.ok(chatService.getListForAdmin(filter));
    }

    @GetMapping(ApiConstants.employeeApi)
    public ResponseEntity<List<EmployeeListLoginDTO>> getEmployeeList(@ParameterObject EmployeeFilter filter) {
        return ResponseEntity.ok(chatService.getEmployeeList(filter));
    }
}
