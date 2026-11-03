package uz.uzkassa.silen.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.silen.domain.Organization;
import uz.uzkassa.silen.domain.User;
import uz.uzkassa.silen.dto.AccountDTO;
import uz.uzkassa.silen.dto.ChildOrganizationInfoDTO;
import uz.uzkassa.silen.dto.SelectItem;
import uz.uzkassa.silen.dto.UserDTO;
import uz.uzkassa.silen.dto.filter.UserFilter;
import uz.uzkassa.silen.enumeration.ServiceType;
import uz.uzkassa.silen.enumeration.UserRole;
import uz.uzkassa.silen.exceptions.BadRequestException;
import uz.uzkassa.silen.repository.pg.OrganizationPermissionRepository;
import uz.uzkassa.silen.repository.pg.RolePermissionRepository;
import uz.uzkassa.silen.repository.pg.UserPermissionRepository;
import uz.uzkassa.silen.repository.pg.UserRepository;
import uz.uzkassa.silen.repository.redis.RedisUserRepository;
import uz.uzkassa.silen.utils.ServerUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService extends BaseService {
    private final UserRepository userRepository;
    private final RedisUserRepository redisUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrganizationPermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final UserPermissionRepository userPermissionRepository;

    @Transactional
    public Optional<User> requestPasswordReset(final String login) {
        return userRepository.findOneByLoginAndDeletedFalse(login)
            .filter(User::isActivated).map(user -> {
                user.setResetKey(ServerUtils.generateResetKey());
                user.setResetDate(LocalDateTime.now());
                userRepository.save(user);
                return user;
            });
    }

    @Transactional
    public Optional<User> completePasswordReset(final String newPassword, final String key) {
        return userRepository.findOneByResetKey(key)
            .filter(user -> user.getResetDate().isAfter(LocalDateTime.now().minusSeconds(86400)))
            .map(user -> {
                final String encryptedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encryptedPassword);
                user.setResetKey(null);
                user.setResetDate(null);
                userRepository.save(user);
                log.info("Password reset for User {}", user.getLogin());
                return user;
            });
    }

    @Transactional
    public void changePassword(final String currentClearTextPassword, final String newPassword) {
        Optional.ofNullable(getCurrentUserLogin())
            .flatMap(userRepository::findOneByLoginAndDeletedFalse)
            .ifPresent(user -> {
                final String currentEncryptedPassword = user.getPassword();
                if (!passwordEncoder.matches(currentClearTextPassword, currentEncryptedPassword)) {
                    throw new BadRequestException("Invalid password");
                }
                final String encryptedPassword = passwordEncoder.encode(newPassword);
                user.setPassword(encryptedPassword);
                userRepository.save(user);
                log.info("Changed password for User: {}", user.getLogin());
            });
    }

    @Transactional
    public UserDTO save(final UserDTO userDTO) {
        if (!isAgency() && userDTO.getOrganizationId() == null) {
            throw badRequest("Организация требуется").get();
        }
        final User user = userRepository.findOneByLoginAndDeletedFalse(userDTO.getLogin()).orElseGet(User::new);
        if (user.getId() != null && (userDTO.getId() == null || !userDTO.getId().equals(user.getId()))) {
            throw badRequest("Пользователь с таким логином уже существует").get();
        }
        if (user.getId() == null && StringUtils.isBlank(userDTO.getPasswordNew())) {
            throw badRequest("Парол требуется").get();
        }
        user.setOrganizationId(userDTO.getOrganizationId());

        if (userDTO.getOrganizationId() != null) {
            organizationRepository.findById(userDTO.getOrganizationId())
                .ifPresent(organization -> user.setOrganizationTin(organization.getTin()));
        }

        user.setLogin(userDTO.getLogin());
        user.setPinfl(userDTO.getPinfl());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        if (StringUtils.isNotBlank(userDTO.getPasswordNew())) {
            final String encryptedPassword = passwordEncoder.encode(userDTO.getPasswordNew());
            user.setPassword(encryptedPassword);
        }
        if (user.getId() == null) {
            user.setActivated(userDTO.isActivated());
        }
        user.setRole(userDTO.getRole());
        userRepository.save(user);
        log.info("Created/Updated Information for User: {}", user.getLogin());
        return user.toDto();
    }

    @Transactional
    public void activateInactivate(final String id) {
        userRepository.findById(id)
            .ifPresent(user -> {
                user.setActivated(!user.isActivated());
                userRepository.save(user);
                log.info("Activated/Inactivated User: {}", user.getLogin());
            });
    }

    public void killToken(final String id) {
        redisUserRepository.findById(id)
            .ifPresent(redisUserRepository::delete);
    }

    public Page<UserDTO> findAll(final UserFilter filter) {
        userRepository.findOneByLoginAndDeletedFalse(getCurrentUserLogin())
            .ifPresent(user -> {
                if (user.getRole().equals(UserRole.USER)) {
                    filter.setLogin(getCurrentUserLogin());
                }
            });
        return userRepository.findAllByFilter(filter).map(User::toDto);
    }

    public List<SelectItem> getItems(final UserFilter filter) {
        return userRepository.findAllByFilter(filter)
            .stream()
            .map(User::toSelectItem)
            .collect(Collectors.toList());
    }

    public UserDTO findOne(final String id) {
        return userRepository.findById(id).map(User::toDto)
            .orElseThrow(() -> new BadRequestException("Пользователь не найден"));
    }

    @Transactional
    public void delete(final String id) {
        userRepository.findById(id)
            .ifPresent(user -> {
                userRepository.delete(user);
                log.info("Deleted User: {}", user.getLogin());
            });
    }

    public AccountDTO getMe() {
        return Optional.ofNullable(getCurrentUserLogin())
            .flatMap(userRepository::findOneByLoginAndDeletedFalse)
            .map(user -> {
                final AccountDTO dto = new AccountDTO();
                dto.setId(user.getId());
                dto.setLogin(user.getLogin());
                dto.setOrganizationId(user.getOrganizationId());
                dto.setFirstName(user.getFirstName());
                dto.setLastName(user.getLastName());
                if (user.getRole() != null) {
                    dto.setRoleName(user.getRole().getText());
                    dto.setRole(user.getRole());
                }
                Set<String> userPermissions = new HashSet<>();
                List<String> allPermissionCodeByUserId = userPermissionRepository.findAllPermissionCodeByUserId(user.getId());
                if (CollectionUtils.isEmpty(allPermissionCodeByUserId)) {
                    userPermissions.addAll(rolePermissionRepository.findAllPermissionCodesByRole(user.getRole()));
                } else {
                    userPermissions.addAll(allPermissionCodeByUserId);
                }
                dto.setAuthorities(userPermissions);
                final Organization organization = user.getOrganization();
                if (organization != null) {
                    dto.setHasChild(organization.isHasChild());
                    dto.setParentId(organization.getParentId());
                    dto.setProductGroup(organization.getProductGroup());
                    dto.setOrganizationTin(organization.getTin());
                    dto.setOrganizationName(organization.getName());
                    if (CollectionUtils.isNotEmpty(organization.getTypes())) {
                        dto.setOrganizationTypes(organization.getTypes());
                    }
                    if (organization.isDemoAllowed()) {
                        dto.setPermissions(Collections.singleton(ServiceType.FREE_TRIAL.name()));
                    } else {
                        dto.setPermissions(new HashSet<>(permissionRepository.findAllPermissionKeysByOrganizationIdAndExpireDateAfter(organization.getId())));
                    }
                    Hibernate.initialize(organization.getGcp());
                    dto.setGcp(organization.getGcp());
                }
                return dto;
            }).orElse(null);
    }

    public Long getActiveUsersByTin(String tin, String info) {
        final Organization organization = organizationRepository.findFistByTinAndDeletedIsFalse(tin).orElseThrow(notFound("Oрганизация не найден"));
        if (info != null) {
            try {
                ChildOrganizationInfoDTO infoDTO = objectMapper.readValue(info, ChildOrganizationInfoDTO.class);
                Organization childOrganization = organizationRepository.findById(infoDTO.getId()).orElseThrow(notFound("Oрганизация не найден"));
                final Long activeUsers = userRepository.countAllByOrganizationIdAndDeletedFalseAndActivatedAndRoleEquals(childOrganization.getId(), true, UserRole.OPERATOR);
                log.info("child organization {},{} active users {}", childOrganization.getTin(), childOrganization.getName(), activeUsers);
                return activeUsers;
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        final Long activeUsers = userRepository.countAllByOrganizationIdAndDeletedFalseAndActivatedAndRoleEquals(organization.getId(), true, UserRole.OPERATOR);
        log.info("organization {},{} active users {}", organization.getTin(), organization.getName(), activeUsers);
        return activeUsers;
    }

    public List<String> getBillingFilterUserIds() {
        final String currentUserId = getCurrentUserId();
        final User user = userRepository.getReferenceById(currentUserId);
        if (user.getRole() == UserRole.SUPERADMIN || user.getRole() == UserRole.CABINET_ADMIN) {
            return null;
        } else if (user.getRole() == UserRole.SUPERVISOR) {
            final List<String> managerIds = userRepository.getSupervisorManagers(currentUserId);
            managerIds.add(currentUserId);
            return managerIds;
        } else {
            return Collections.singletonList(currentUserId);
        }
    }

    public List<String> getBillingFilterUserTins() {
        final String currentUserId = getCurrentUserId();
        final User user = userRepository.getReferenceById(currentUserId);
        if (user.getRole() == UserRole.SUPERADMIN || user.getRole() == UserRole.CABINET_ADMIN) {
            return null;
        } else if (user.getRole() == UserRole.SUPERVISOR) {
            final List<String> managerIds = userRepository.getSupervisorManagers(currentUserId);
            managerIds.add(currentUserId);
            return organizationRepository.findTinsByManagerIds(managerIds);
        } else {
            return organizationRepository.findTinsByManagerIds(Collections.singletonList(currentUserId));
        }
    }
}
