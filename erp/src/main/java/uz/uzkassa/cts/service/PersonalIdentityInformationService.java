package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.PersonalIdentityInformation;
import uz.uzkassa.cts.dto.personalidentityinformation.PersonalIdentityInformationDTO;

import java.util.List;

public interface PersonalIdentityInformationService extends BaseService<PersonalIdentityInformation, Long> {

    Long create(Long employeeId, PersonalIdentityInformationDTO personalIdentityInformationDTO);

    Long update(Long employeeId, PersonalIdentityInformationDTO personalIdentityInformationDTO);

    List<PersonalIdentityInformationDTO> getList(Long employeeId);

    PersonalIdentityInformationDTO get(Long employeeId);
}
