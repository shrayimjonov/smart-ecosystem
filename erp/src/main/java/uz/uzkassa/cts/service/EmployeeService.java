package uz.uzkassa.cts.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.dto.auth.ChangePasswordDTO;
import uz.uzkassa.cts.dto.auth.ChangeUsernameConfirmDTO;
import uz.uzkassa.cts.dto.auth.SecretKeyDTO;
import uz.uzkassa.cts.dto.auth.UsernameDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.employee.admin.EmployeeDetailAdminDTO;
import uz.uzkassa.cts.dto.employee.admin.EmployeeListAdminDTO;
import uz.uzkassa.cts.dto.employee.base.EmployeeDTO;
import uz.uzkassa.cts.dto.employee.cabinet.EmployeeDetailCabinetDTO;
import uz.uzkassa.cts.dto.employee.cabinet.EmployeeListCabinetDTO;
import uz.uzkassa.cts.dto.file.FileDTO;
import uz.uzkassa.cts.enums.UserStatus;
import uz.uzkassa.cts.filter.EmployeeFilter;

import java.util.List;

public interface EmployeeService extends BaseService<User, Long> {

    Long create(EmployeeDTO employeeDTO);

    Long update(Long id, EmployeeDTO employeeDTO);

    EmployeeDetailCabinetDTO getForCabinet(Long id);

    EmployeeDetailAdminDTO getForAdmin(Long id);

    Page<EmployeeListCabinetDTO> getListForCabinet(EmployeeFilter filter);

    Page<EmployeeListAdminDTO> getListForAdmin(EmployeeFilter filter);

    List<CommonDTO> lookUp(EmployeeFilter filter);

    void updateStatus(Long id, UserStatus status);

    FileDTO uploadPhoto(Long id, MultipartFile photo);

    FileDTO uploadTimeTrackPhoto(Long id, MultipartFile timeTrackPhoto);

    void removePhoto(Long id, Long photoId);

    void removeTimeTrackPhoto(Long id, Long timeTrackPhotoId);

    SecretKeyDTO changePasswordInit(Long id);

    void changePasswordConfirm(Long id, ChangePasswordDTO changePasswordDTO);

    SecretKeyDTO changeUsernameInit(Long id, UsernameDTO usernameDTO);

    void changeUsernameConfirm(Long id, ChangeUsernameConfirmDTO changeLoginConfirmDTO);

    List<User> getTodayBirthdays();
}
