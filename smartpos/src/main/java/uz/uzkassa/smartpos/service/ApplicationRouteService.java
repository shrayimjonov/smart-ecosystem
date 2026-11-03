package uz.uzkassa.smartpos.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.common.ApplicationRoute;
import uz.uzkassa.smartpos.domain.postgres.user.Permission;
import uz.uzkassa.smartpos.domain.postgres.user.User;
import uz.uzkassa.smartpos.domain.postgres.user.UserPermission;
import uz.uzkassa.smartpos.dto.applicationroute.request.ApplicationRouteRequestDto;
import uz.uzkassa.smartpos.dto.applicationroute.response.ApplicationRoutUserPermissions;
import uz.uzkassa.smartpos.dto.applicationroute.response.ApplicationRouteListResponseDto;
import uz.uzkassa.smartpos.dto.applicationroute.response.ApplicationRouteResponseDto;
import uz.uzkassa.smartpos.exceptions.EntityAlreadyExistException;
import uz.uzkassa.smartpos.mappers.ApplicationRouterMapper;
import uz.uzkassa.smartpos.repository.postgres.common.ApplicationRouteRepository;
import uz.uzkassa.smartpos.repository.postgres.user.PermissionRepository;
import uz.uzkassa.smartpos.repository.postgres.user.UserPermissionRepository;
import uz.uzkassa.smartpos.repository.postgres.user.UserRepository;
import uz.uzkassa.smartpos.security.AuthoritiesConstants;
import uz.uzkassa.smartpos.security.SecurityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationRouteService extends SmartposService {
    private final ApplicationRouteRepository applicationRouteRepository;
    private final PermissionRepository permissionRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final ApplicationRouterMapper applicationRouterMapper;
    private final UserRepository userRepository;

    @Transactional
    public ApplicationRouteResponseDto create(ApplicationRouteRequestDto dto) {

        if (applicationRouteRepository.findByAnnotationName(dto.getAnnotationName()).isPresent()) {
            throw new EntityAlreadyExistException(localizationService.getMessage("permission.already.exists", dto.getAnnotationName()));
        }

        ApplicationRoute route = applicationRouterMapper.toEntity(dto);
        applicationRouteRepository.save(route);

        createPermission(route.getAnnotationName());

        return applicationRouterMapper.toDto(route);
    }

    private void createPermission(String code) {
        Permission permission = permissionRepository.findByCode(code).orElseGet(Permission::new);
        if (permission.getCode() == null) {
            permission = new Permission();
            permission.setCode(code);
            permissionRepository.save(permission);

            User user = userRepository.findById(SecurityUtils.getCurrentUserId()).orElseThrow(notFound(User.class.getSimpleName()));
            if (user.getAuthorities().stream().anyMatch(role -> AuthoritiesConstants.SMARTPOS_ADMIN.equals(role.getName()))) {

                UserPermission userPermission = userPermissionRepository.findByUserIdAndPermissionCode(user.getId(), code).orElseGet(UserPermission::new);
                if (userPermission.getId() == null) {
                    userPermission = new UserPermission();
                    userPermission.setUserId(user.getId());
                    userPermission.setHasAccess(true);
                    userPermission.setPermissionCode(permission.getCode());
                    userPermissionRepository.save(userPermission);
                }
            }

            this.clearLoginCaches(user.getLogin());
        }
    }


    @Transactional
    public ApplicationRouteResponseDto update(ApplicationRouteRequestDto dto) {

        if (applicationRouteRepository.findByAnnotationNameAndIdNot(dto.getAnnotationName(), dto.getId()).isPresent()) {
            throw new EntityAlreadyExistException(localizationService.getMessage("permission.already.exists", dto.getAnnotationName()));
        }

        ApplicationRoute route = applicationRouteRepository.findById(dto.getId()).orElseThrow(notFound(ApplicationRoute.class.getSimpleName(), dto.getId()));
        applicationRouterMapper.update(dto, route);
        applicationRouteRepository.save(route);

        createPermission(route.getAnnotationName());

        return applicationRouterMapper.toDto(route);
    }

    @Transactional
    public void deleteById(Long id) {
        ApplicationRoute applicationRoute = applicationRouteRepository.findById(id).orElseThrow(notFound(ApplicationRoute.class.getSimpleName()));
        deletePermission(applicationRoute.getAnnotationName());
        applicationRouteRepository.delete(applicationRoute);
    }

    private void deletePermission(String code) {
        permissionRepository.findByCode(code).ifPresent(permission -> {
            userPermissionRepository.deleteAllByPermissionCode(permission.getCode());
            permissionRepository.delete(permission);
        });
    }

    public List<ApplicationRouteResponseDto> list(Long parentId) {
        List<ApplicationRoute> routes = applicationRouteRepository.findByParentIdOrderByPositionAsc(parentId);
        return routes.stream().map(applicationRouterMapper::toDto).collect(Collectors.toList());
    }


    public List<ApplicationRouteListResponseDto> getAll() {
        return fill(applicationRouteRepository.findByParentIdOrderByPosition(null));
    }

    private List<ApplicationRouteListResponseDto> fill(Set<ApplicationRoute> routes) {
        List<ApplicationRouteListResponseDto> dtos = new ArrayList<>();
        routes.forEach(route -> {
            ApplicationRouteListResponseDto dto = applicationRouterMapper.toListDto(route);
            dto.setSubordinates(fill(route.getChildren()));
            dtos.add(dto);
        });
        return dtos;
    }


    public List<ApplicationRoutUserPermissions> getAllWithUserPermission(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(notFound(User.class.getSimpleName(), userId));
        return fillWithAuth(applicationRouteRepository.findByParentIdOrderByPosition(null), user.getPermissions());
    }


    public ApplicationRouteResponseDto getById(Long id) {
        return applicationRouterMapper.toDto(applicationRouteRepository.findById(id).orElseThrow(notFound(ApplicationRoute.class.getSimpleName(), id)));
    }

    private List<ApplicationRoutUserPermissions> fillWithAuth(Set<ApplicationRoute> routes, List<Permission> permissions) {
        List<ApplicationRoutUserPermissions> dtos = new ArrayList<>();
        routes.forEach(route -> {
            ApplicationRoutUserPermissions dto = applicationRouterMapper.toPermissionListDto(route);
            permissions.stream().map(permission -> permission.getCode().contains(route.getAnnotationName())).forEach(dto::setAllowed);
            dto.setChildren(fillWithAuth(route.getChildren(), permissions));
            dtos.add(dto);
        });
        return dtos;
    }
}
