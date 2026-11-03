package uz.uzkassa.smartpossupply.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.company.Company;
import uz.uzkassa.smartpossupply.domain.superuser.UserCompany;
import uz.uzkassa.smartpossupply.dto.auth.TokenDTO;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.dto.user.UserCompanyAuthDTO;
import uz.uzkassa.smartpossupply.dto.user.UserCompanyDTO;
import uz.uzkassa.smartpossupply.dto.user.UserCompanyListDTO;
import uz.uzkassa.smartpossupply.dto.user.UserInfoDTO;
import uz.uzkassa.smartpossupply.filters.SimpleFilter;
import uz.uzkassa.smartpossupply.repository.CompanyRepository;
import uz.uzkassa.smartpossupply.repository.UserCompanyRepository;
import uz.uzkassa.smartpossupply.security.jwt.TokenProvider;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class UserCompanyService {
    UserCompanyRepository userCompanyRepository;
    TokenProvider tokenProvider;
    CompanyRepository companyRepository;
    ValidationService validationService;

    @Transactional
    public void create(UserCompanyDTO userCompanyDTO) {
        if (SecurityUtils.getCurrentCompanyId() != null) {
            userCompanyDTO.getCompanyIds().add(SecurityUtils.getCurrentCompanyId());
        }
        Map<Long, UserCompany> userCompanyMap = userCompanyRepository.findAllByUserId(userCompanyDTO.getUserId())
                .stream()
                .collect(Collectors.toMap(UserCompany::getCompanyId, Function.identity()));

        List<UserCompany> userCompanies = userCompanyDTO.getCompanyIds().stream().map(companyId -> {
            UserCompany userCompany = userCompanyMap.get(companyId) != null ? userCompanyMap.get(companyId) : new UserCompany();
            if (userCompany.getId() == null) {
                userCompany.setUserId(userCompanyDTO.getUserId());
                userCompany.setCompanyId(companyId);
            }
            userCompanyMap.remove(companyId);
            return userCompany;
        }).toList();

        userCompanyRepository.saveAll(userCompanies);
        if (!CollectionUtils.isEmpty(userCompanyMap)) {
            userCompanyRepository.deleteAll(userCompanyMap.values());
        }
    }

    @Transactional
    public Page<UserCompanyListDTO> getList(Long userId, SimpleFilter filter) {
        return userCompanyRepository.findAllByUserId(userId, filter.getSortedPageable()).map(userCompany -> {
            UserCompanyListDTO userCompanyListDTO = new UserCompanyListDTO();
            userCompanyListDTO.setCompany(userCompany.getCompany().toCommonDTO());
            userCompanyListDTO.setCreator(userCompany.getCreator().toCommonDTO());
            userCompanyListDTO.setUpdater(userCompany.getUpdater().toCommonDTO());
            userCompanyListDTO.setCreatedDate(userCompany.getCreatedDate());
            userCompanyListDTO.setUpdatedDate(userCompany.getUpdatedDate());
            return userCompanyListDTO;
        });
    }

    public Page<UserInfoDTO> getUsers(SimpleFilter filter) {
        Pageable sortedPageable = filter.getSortedPageable();
        return userCompanyRepository.findAllUsers(sortedPageable).map(user -> {
            UserInfoDTO userInfoDTO = new UserInfoDTO();
            userInfoDTO.setId(user.getId());
            userInfoDTO.setName(user.getName());
            userInfoDTO.setPhone(user.getPhone());
            userInfoDTO.setRole(user.getRole());
            userInfoDTO.setStatus(user.getStatus());
            return userInfoDTO;
        });
    }

    public List<CommonDTO> getCurrentUserCompanies() {
        return userCompanyRepository.findAllByUserId(SecurityUtils.getCurrentUserId()).stream()
                .map(userCompany -> userCompany.getCompany().toCommonDTO()).toList();
    }

    public TokenDTO changeCompany(UserCompanyAuthDTO userCompanyAuthDTO) {
        Company company = companyRepository.findById(userCompanyAuthDTO.getCompanyId())
                .orElseThrow(validationService.entityNotFoundExceptionThrow(Company.class.getSimpleName(), "id", userCompanyAuthDTO.getCompanyId()));
        String token = tokenProvider.createToken(SecurityContextHolder.getContext().getAuthentication(), company.getId(), company.getTin(), false);
        return new TokenDTO(token);
    }
}
