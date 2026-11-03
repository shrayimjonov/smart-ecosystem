package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.hrms.Chat;
import uz.uzkassa.cts.dto.chat.ChatDTO;
import uz.uzkassa.cts.dto.chat.admin.ChatListAdminDTO;
import uz.uzkassa.cts.dto.employee.EmployeeListLoginDTO;
import uz.uzkassa.cts.enums.EntityTypeEnum;
import uz.uzkassa.cts.filter.BaseFilter;
import uz.uzkassa.cts.filter.EmployeeFilter;

import java.util.List;

public interface ChatService extends BaseService<Chat, Long> {

    void create(ChatDTO chatDTO);

    void upload(MultipartFile file, Long entityId, EntityTypeEnum entityType, String message, List<Long> memberIds);

    List<EmployeeListLoginDTO> getEmployeeList(EmployeeFilter filter);

    Page<ChatListAdminDTO> getListForAdmin(BaseFilter filter);
}
