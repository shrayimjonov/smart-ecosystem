package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.domain.FileUpload;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.domain.base.SimpleEntity;
import uz.uzkassa.cts.domain.permission.Authority;
import uz.uzkassa.cts.dto.auth.ChangePasswordDTO;
import uz.uzkassa.cts.dto.auth.ChangeUsernameConfirmDTO;
import uz.uzkassa.cts.dto.auth.SecretKeyDTO;
import uz.uzkassa.cts.dto.auth.UsernameDTO;
import uz.uzkassa.cts.dto.base.CommonDTO;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.contact.ContactDTO;
import uz.uzkassa.cts.dto.employee.EmployeeListPhotoDTO;
import uz.uzkassa.cts.dto.employee.admin.EmployeeDetailAdminDTO;
import uz.uzkassa.cts.dto.employee.admin.EmployeeListAdminDTO;
import uz.uzkassa.cts.dto.employee.base.EmployeeDTO;
import uz.uzkassa.cts.dto.employee.cabinet.EmployeeDetailCabinetDTO;
import uz.uzkassa.cts.dto.employee.cabinet.EmployeeListCabinetDTO;
import uz.uzkassa.cts.dto.file.FileDTO;
import uz.uzkassa.cts.dto.payload.UserRolePermissionPayload;
import uz.uzkassa.cts.enums.DocumentType;
import uz.uzkassa.cts.enums.ReferenceType;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.enums.UserStatus;
import uz.uzkassa.cts.filter.EmployeeFilter;
import uz.uzkassa.cts.integrations.AiTimeTrackClient;
import uz.uzkassa.cts.rabbitmq.producer.RabbitMqPermissionProducer;
import uz.uzkassa.cts.repository.*;
import uz.uzkassa.cts.service.*;
import uz.uzkassa.cts.utils.RandomUtils;
import uz.uzkassa.cts.validator.ValidationContext;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Transactional(readOnly = true)
@Service
public class EmployeeServiceImpl extends BaseServiceImpl<User, Long> implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final ReferenceRepository referenceRepository;
    private final PasswordEncoder passwordEncoder;
    private final ContactService contactService;
    private final FileUploadService fileUploadService;
    private final RabbitMqPermissionProducer rabbitMqPermissionProducer;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserPermissionService userPermissionService;
    private final UserAuthService userAuthService;
    private final CacheService cacheService;
    private final CustomerRepository customerRepository;
    private final AiTimeTrackClient aiTimeTrackClient;
    private final ValidationContext validationContext;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               UserRepository userRepository, AuthorityRepository authorityRepository,
                               ReferenceRepository referenceRepository, PasswordEncoder passwordEncoder,
                               ContactService contactService, FileUploadService fileUploadService,
                               RabbitMqPermissionProducer rabbitMqPermissionProducer,
                               RolePermissionRepository rolePermissionRepository, UserPermissionService userPermissionService,
                               UserAuthService userAuthService, CacheService cacheService, CustomerRepository customerRepository,
                               AiTimeTrackClient aiTimeTrackClient, ValidationContext validationContext) {
        super(employeeRepository);
        this.employeeRepository = employeeRepository;
        this.userRepository = userRepository;
        this.authorityRepository = authorityRepository;
        this.referenceRepository = referenceRepository;
        this.passwordEncoder = passwordEncoder;
        this.contactService = contactService;
        this.fileUploadService = fileUploadService;
        this.rabbitMqPermissionProducer = rabbitMqPermissionProducer;
        this.rolePermissionRepository = rolePermissionRepository;
        this.userPermissionService = userPermissionService;
        this.userAuthService = userAuthService;
        this.cacheService = cacheService;
        this.customerRepository = customerRepository;
        this.aiTimeTrackClient = aiTimeTrackClient;
        this.validationContext = validationContext;
    }

    @Override
    @Transactional
    public Long create(EmployeeDTO employeeDTO) {
        validationContext.validate(employeeDTO);

        Set<Authority> authorities = new HashSet<>(authorityRepository.findAllById(employeeDTO.getRoles().stream().map(Role::getRoleCode).collect(Collectors.toSet())));
        String phone = employeeDTO.getPhones().stream().filter(ContactDTO::isPrimary).toList().getFirst().getName();
        String email = CollectionUtils.isEmpty(employeeDTO.getEmails()) ? null : employeeDTO.getEmails().stream().filter(ContactDTO::isPrimary).toList().getFirst().getName();
        final String password = StringUtils.isNotEmpty(employeeDTO.getPassword()) ? employeeDTO.getPassword() : RandomUtils.generatePassword();

        User employee = new User();
        employee.setFirstName(employeeDTO.getFirstName());
        employee.setLastName(employeeDTO.getLastName());
        employee.setPatronymic(employeeDTO.getPatronymic());
        employee.setLogin(employeeDTO.getUsername());
        employee.setBirthday(employeeDTO.getBirthday());
        employee.setPhone(phone);
        employee.setEmail(email);
        employee.setStatus(UserStatus.ACTIVE);
        employee.setAuthorities(authorities);
        employee.setPassword(passwordEncoder.encode(password));
        userRepository.save(employee);
        contactService.createBatch(employee.getEntityType(), employee.getEntityId(), employeeDTO.getPhones());
        if (CollectionUtils.isNotEmpty(employeeDTO.getEmails())) {
            contactService.createBatch(employee.getEntityType(), employee.getEntityId(), employeeDTO.getEmails());
        }
        employeeDTO.getRoles().forEach(role -> rabbitMqPermissionProducer.sendToQueue(new UserRolePermissionPayload(employee.getId(), rolePermissionRepository.findAllPermissionIdsByRole(role))));
        cacheService.clearByEntityType(employee.getEntityType());
        return employee.getId();
    }

    @Override
    @Transactional
    public Long update(Long id, EmployeeDTO employeeDTO) {
        employeeDTO.setId(id);
        validationContext.validate(employeeDTO);
        return employeeRepository.findById(id).map(employee -> {
            String email = CollectionUtils.isEmpty(employeeDTO.getEmails()) ? null : employeeDTO.getEmails().stream().filter(ContactDTO::isPrimary).toList().getFirst().getName();
            String phone = employeeDTO.getPhones().stream().filter(ContactDTO::isPrimary).toList().getFirst().getName();
            Set<Authority> authorities = new HashSet<>(authorityRepository.findAllById(employeeDTO.getRoles().stream().map(Role::getRoleCode).collect(Collectors.toSet())));

            List<Role> oldRoles = employee.getAuthorities().stream().map(authority -> Role.getRoleCode(authority.getName())).toList();
            List<Role> newRoles = employeeDTO.getRoles();
            List<Role> roles = new ArrayList<>(newRoles);
            roles.removeAll(oldRoles);

            employee.setFirstName(employeeDTO.getFirstName());
            employee.setLastName(employeeDTO.getLastName());
            employee.setPatronymic(employeeDTO.getPatronymic());
            employee.setBirthday(employeeDTO.getBirthday());
            employee.setPhone(phone);
            employee.setEmail(email);
            employee.getAuthorities().clear();
            employee.setAuthorities(authorities);
            userRepository.save(employee);
            contactService.updateBatch(employee.getEntityType(), employee.getEntityId(), employeeDTO.getPhones());
            if (CollectionUtils.isNotEmpty(employeeDTO.getEmails())) {
                contactService.updateBatch(employee.getEntityType(), employee.getEntityId(), employeeDTO.getEmails());
            }
            if (CollectionUtils.isNotEmpty(roles)) {
                userPermissionService.deleteAllByUserId(employee.getId());
                roles.forEach(role -> rabbitMqPermissionProducer.sendToQueue(new UserRolePermissionPayload(employee.getId(), rolePermissionRepository.findAllPermissionIdsByRole(role))));
            }
            cacheService.clearByEntityType(employee.getEntityType());
            return employee.getId();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(USER));
    }

    @Override
    public Page<EmployeeListCabinetDTO> getListForCabinet(EmployeeFilter filter) {
        ResultList<User> resultList = employeeRepository.getResultList(filter);
        List<EmployeeListCabinetDTO> result = resultList.getList().stream().map(employee -> {
            EmployeeListCabinetDTO employeeListDTO = new EmployeeListCabinetDTO();
            employeeListDTO.setId(employee.getId());
            employeeListDTO.setFirstName(employee.getFirstName());
            employeeListDTO.setLastName(employee.getLastName());
            employeeListDTO.setPatronymic(employee.getPatronymic());
            employeeListDTO.setPhone(employee.getPhone());
            employeeListDTO.setUsername(employee.getLogin());
            employeeListDTO.setRoles(employee.getAuthorities().stream().map(authority -> Role.getRoleCode(authority.getName())).toList());
            employeeListDTO.setStatus(employee.getStatus());
            if (employee.getBranchId() != null) {
                employeeListDTO.setBranch(employee.getBranch().toCommonDTO());
            }
            if (employee.getDepartmentId() != null) {
                employeeListDTO.setDepartment(employee.getDepartment().toCommonDTO());
            }
            if (employee.getPositionId() != null) {
                employeeListDTO.setPosition(employee.getPosition().toCommonDTO());
            }
            return employeeListDTO;
        }).collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public Page<EmployeeListAdminDTO> getListForAdmin(EmployeeFilter filter) {
        filter.setRoles(Role.getCompanyAndCabinetRoleCodes());
        ResultList<User> resultList = employeeRepository.getResultList(filter);
        List<EmployeeListAdminDTO> result = resultList.getList().stream().map(employee -> {
            EmployeeListAdminDTO employeeListDTO = new EmployeeListAdminDTO();
            employeeListDTO.setId(employee.getId());
            employeeListDTO.setFirstName(employee.getFirstName());
            employeeListDTO.setLastName(employee.getLastName());
            employeeListDTO.setPatronymic(employee.getPatronymic());
            employeeListDTO.setPhone(employee.getPhone());
            employeeListDTO.setUsername(employee.getLogin());
            employeeListDTO.setRoles(employee.getAuthorities().stream().map(authority -> Role.getRoleCode(authority.getName())).toList());
            employeeListDTO.setStatus(employee.getStatus());
            if (employee.getCompanyId() != null) {
                employeeListDTO.setCompany(employee.getCompany().toCommonDTO());
            }
            if (employee.getBranchId() != null) {
                employeeListDTO.setBranch(employee.getBranch().toCommonDTO());
            }
            if (employee.getDepartmentId() != null) {
                employeeListDTO.setDepartment(employee.getDepartment().toCommonDTO());
            }
            if (employee.getPositionId() != null) {
                employeeListDTO.setPosition(employee.getPosition().toCommonDTO());
            }
            employeeListDTO.setCreatedDate(employee.getCreatedDate());
            return employeeListDTO;
        }).collect(Collectors.toList());
        return new PageImpl<>(result, filter.getOrderedPageable(), resultList.getCount());
    }

    @Override
    public List<CommonDTO> lookUp(EmployeeFilter filter) {
        if (CollectionUtils.isEmpty(filter.getRoles())) {
            filter.setRoles(Role.getCompanyAndCabinetRoleCodes());
        }
        return employeeRepository.getResultList(filter).getList().stream().map(SimpleEntity::toCommonDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Long id, UserStatus status) {
        employeeRepository.findById(id).ifPresentOrElse(employee -> {
            employee.setStatus(status);
            employeeRepository.save(employee);
            if (UserStatus.BLOCK == status) {
                customerRepository.changeManager(id, null);
            }
            cacheService.clearByEntityType(employee.getEntityType());
        }, () -> validationService.entityNotFoundExceptionThrow(USER).get());
    }

    @Override
    @Transactional
    public FileDTO uploadPhoto(Long id, MultipartFile photo) {
        return employeeRepository.findById(id).map(employee -> {
            FileUpload profilePhoto = fileUploadService.upload(photo, employee.getEntityType(), employee.getEntityId(), DocumentType.EMPLOYEE_PROFILE_PHOTO);
            employee.setPhotoId(profilePhoto.getId());
            employee.setPhoto(profilePhoto);
            employeeRepository.save(employee);
            cacheService.clearByEntityType(employee.getEntityType());
            return profilePhoto.toFileDTO();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(USER));
    }

    @Override
    @Transactional
    public FileDTO uploadTimeTrackPhoto(Long id, MultipartFile timeTrackPhoto) {
        return employeeRepository.findById(id).map(employee -> {
            Optional.ofNullable(employee.getTimeTrackPhotoId()).ifPresent(fileUploadService::deleteById);
            FileUpload employeeTimeTrackPhoto = fileUploadService.upload(timeTrackPhoto, employee.getEntityType(), employee.getEntityId(), DocumentType.EMPLOYEE_TIME_TRACK_PHOTO);
            employee.setTimeTrackPhotoId(employeeTimeTrackPhoto.getId());
            employee.setTimeTrackPhoto(employeeTimeTrackPhoto);
            employeeRepository.save(employee);
            cacheService.clearByEntityType(employee.getEntityType());
            Thread.startVirtualThread(() -> {
                EmployeeListPhotoDTO employeeListPhotoDTO = new EmployeeListPhotoDTO();
                employeeListPhotoDTO.setId(id);
                employeeListPhotoDTO.setFirstName(employee.getFirstName());
                employeeListPhotoDTO.setLastName(employee.getLastName());
                employeeListPhotoDTO.setPatronymic(employee.getPatronymic());
                employeeListPhotoDTO.setPhoto(employeeTimeTrackPhoto.toFileDTO());
                aiTimeTrackClient.syncEmployee(employeeListPhotoDTO);
            });
            return employeeTimeTrackPhoto.toFileDTO();
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(USER));
    }

    @Override
    @Transactional
    public void removePhoto(Long id, Long photoId) {
        employeeRepository.findById(id).ifPresentOrElse(employee -> {
            employee.setPhotoId(null);
            employee.setPhoto(null);
            fileUploadService.deleteById(photoId);
            employeeRepository.save(employee);
            cacheService.clearByEntityType(employee.getEntityType());
        }, () -> validationService.entityNotFoundExceptionThrow(USER).get());
    }

    @Override
    @Transactional
    public void removeTimeTrackPhoto(Long id, Long timeTrackPhotoId) {
        employeeRepository.findById(id).ifPresentOrElse(employee -> {
            employee.setTimeTrackPhotoId(null);
            employee.setTimeTrackPhoto(null);
            fileUploadService.deleteById(timeTrackPhotoId);
            employeeRepository.save(employee);
            cacheService.clearByEntityType(employee.getEntityType());
        }, () -> validationService.entityNotFoundExceptionThrow(USER).get());
    }

    @Override
    public EmployeeDetailCabinetDTO getForCabinet(Long id) {
        return userRepository.findById(id).map(employee -> {
            EmployeeDetailCabinetDTO employeeDetailDTO = new EmployeeDetailCabinetDTO();
            employeeDetailDTO.setId(id);
            employeeDetailDTO.setFirstName(employee.getFirstName());
            employeeDetailDTO.setLastName(employee.getLastName());
            employeeDetailDTO.setPatronymic(employee.getPatronymic());
            employeeDetailDTO.setUsername(employee.getLogin());
            employeeDetailDTO.setRoles(employee.getAuthorities().stream().map(authority -> Role.getRoleCode(authority.getName())).collect(Collectors.toList()));
            employeeDetailDTO.setPhone(employee.getPhone());
            employeeDetailDTO.setEmail(employee.getEmail());
            employeeDetailDTO.setStatus(employee.getStatus());
            if (employee.getBranchId() != null) {
                employeeDetailDTO.setBranch(employee.getBranch().toCommonDTO());
            }
            if (employee.getDepartmentId() != null) {
                employeeDetailDTO.setDepartment(employee.getDepartment().toCommonDTO());
            }
            if (employee.getPositionId() != null) {
                employeeDetailDTO.setPosition(employee.getPosition().toCommonDTO());
            }
            if (employee.getPhotoId() != null) {
                employeeDetailDTO.setPhoto(employee.getPhoto().toFileDTO());
            }

            List<Long> phoneTypeIds = referenceRepository.findAllIdsByTypeAndDeletedIsFalse(ReferenceType.PHONE_TYPE);
            List<Long> emailTypeIds = referenceRepository.findAllIdsByTypeAndDeletedIsFalse(ReferenceType.EMAIL_TYPE);

            employeeDetailDTO.setPhones(contactService.getList(employee.getEntityType(), employee.getEntityId(), phoneTypeIds));
            employeeDetailDTO.setEmails(contactService.getList(employee.getEntityType(), employee.getEntityId(), emailTypeIds));
            return employeeDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(USER));
    }

    @Override
    public EmployeeDetailAdminDTO getForAdmin(Long id) {
        return userRepository.findById(id).map(employee -> {
            EmployeeDetailAdminDTO employeeDetailDTO = new EmployeeDetailAdminDTO();
            employeeDetailDTO.setId(id);
            employeeDetailDTO.setFirstName(employee.getFirstName());
            employeeDetailDTO.setLastName(employee.getLastName());
            employeeDetailDTO.setPatronymic(employee.getPatronymic());
            employeeDetailDTO.setUsername(employee.getLogin());
            employeeDetailDTO.setRoles(employee.getAuthorities().stream().map(authority -> Role.getRoleCode(authority.getName())).collect(Collectors.toList()));
            employeeDetailDTO.setPhone(employee.getPhone());
            employeeDetailDTO.setEmail(employee.getEmail());
            if (employee.getPhotoId() != null) {
                employeeDetailDTO.setPhoto(employee.getPhoto().toFileDTO());
            }

            List<Long> phoneTypeIds = referenceRepository.findAllIdsByTypeAndDeletedIsFalse(ReferenceType.PHONE_TYPE);
            List<Long> emailTypeIds = referenceRepository.findAllIdsByTypeAndDeletedIsFalse(ReferenceType.EMAIL_TYPE);

            employeeDetailDTO.setPhones(contactService.getList(employee.getEntityType(), employee.getEntityId(), phoneTypeIds));
            employeeDetailDTO.setEmails(contactService.getList(employee.getEntityType(), employee.getEntityId(), emailTypeIds));

            return employeeDetailDTO;
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(USER));
    }

    @Override
    @Transactional
    public SecretKeyDTO changePasswordInit(Long id) {
        return employeeRepository.findById(id).map(userAuthService::changePasswordInit).orElseThrow(validationService.entityNotFoundExceptionThrow(USER));
    }

    @Override
    @Transactional
    public void changePasswordConfirm(Long id, ChangePasswordDTO changePasswordDTO) {
        employeeRepository.findById(id).ifPresentOrElse(user -> userAuthService.changePasswordConfirm(user, changePasswordDTO), () -> validationService.entityNotFoundExceptionThrow(USER).get());
    }

    @Override
    @Transactional
    public SecretKeyDTO changeUsernameInit(Long id, UsernameDTO usernameDTO) {
        return employeeRepository.findById(id).map(user -> userAuthService.changeUsernameInit(user, usernameDTO)).orElseThrow(validationService.entityNotFoundExceptionThrow(USER));
    }

    @Override
    @Transactional
    public void changeUsernameConfirm(Long id, ChangeUsernameConfirmDTO changeLoginConfirmDTO) {
        employeeRepository.findById(id).ifPresentOrElse(user -> userAuthService.changeUsernameConfirm(user, changeLoginConfirmDTO, null, null), () -> validationService.entityNotFoundExceptionThrow(USER).get());
    }

    @Override
    public List<User> getTodayBirthdays() {
        return userRepository.findAllByStatusAndBirthdayAndAuthoritiesInAndDeletedIsFalse(UserStatus.ACTIVE, LocalDate.now(), Set.copyOf(authorityRepository.findAllById(Role.getCompanyAndCabinetRoleCodes())));
    }
}
