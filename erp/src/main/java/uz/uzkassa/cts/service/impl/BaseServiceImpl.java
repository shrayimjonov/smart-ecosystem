package uz.uzkassa.cts.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Table;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.Nullable;
import uz.uzkassa.cts.constants.MessageConstants;
import uz.uzkassa.cts.constants.RegexConstants;
import uz.uzkassa.cts.domain.User;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.repository.BaseRepository;
import uz.uzkassa.cts.repository.UserRepository;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.BaseService;
import uz.uzkassa.cts.service.ValidationService;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

import java.lang.reflect.ParameterizedType;
import java.util.*;

@Slf4j
public abstract class BaseServiceImpl<T, ID> implements BaseService<T, ID>, MessageConstants {

    @PersistenceContext
    protected EntityManager entityManager;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ValidationService validationService;

    protected final BaseRepository<T, ID> repository;

    public BaseServiceImpl(BaseRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Deprecated
    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Page<T> findAll(@Nullable Specification<T> specification, Pageable pageable) {
        return repository.findAll(specification, pageable);
    }

    @Override
    public T save(T entity) {
        return repository.save(entity);
    }

    @Override
    public void saveAll(Iterable<T> coll) {
        repository.saveAll(coll);
    }

    @Override
    public T saveAndFlash(T entity) {
        return repository.saveAndFlush(entity);
    }

    @Override
    public T findById(ID id) {
        return repository.findById(id).orElseThrow(validationService.entityNotFoundExceptionThrow(formatType()));
    }

    private String formatType() {
        String string = ((Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0]).getSimpleName();
        return string.replaceAll(RegexConstants.CLASS_NAME_DOT_FORMAT, ".$1").toLowerCase();
    }

    @Override
    public void delete(T entity) {
        repository.delete(entity);
    }

    @Override
    public void deleteById(ID id) {
        this.deleteById(id, false);
    }

    @Override
    public void deleteById(ID id, boolean check) {
        if (check) {
            this.validateEntityReferences(id);
        }
        repository.deleteById(id);
    }

    public void validateEntityReferences(ID id) {
        String tableName = getTableName();
        List<String[]> referencedTables = repository.findReferencedTables(tableName, id);

        if (!referencedTables.stream().allMatch(Objects::isNull)) {
            List<String> entityNames = mapTablesToEntities(referencedTables);
            throw new BadRequestException("Объект не может быть удален из-за ссылок: " + Arrays.toString(entityNames.toArray()));
        }
    }

    @Override
    public long count() {
        return repository.count();
    }

    protected User getCurrentUserDomain() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(validationService.unauthorizedExceptionThrow());
        return userRepository
                .findOneWithAuthoritiesByLoginAndDeletedIsFalse(login)
                .orElseThrow(validationService.entityNotFoundExceptionThrow(USER, LOGIN, login));
    }

    protected static Long getCompanyId() {
        return SecurityUtils.getCompanyId();
    }

    protected static Long getBranchId() {
        return SecurityUtils.getBranchId();
    }

    protected static Long getCompanyId(Long companyId) {
        return Optional.ofNullable(companyId).orElse(getCompanyId());
    }

    protected static Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    protected static boolean isSuperAdmin() {
        return SecurityUtils.isCurrentUserInRole(Role.ADMIN.getRoleCode());
    }

    protected static boolean isAdmin() {
        return SecurityUtils.isCurrentUserInRole(Role.ADMIN.getRoleCode()) && !SecurityUtils.isCurrentUserInRole(Role.SUPER_ADMIN.getRoleCode());
    }

    protected static boolean isManager() {
        return SecurityUtils.isCurrentUserInRole(Role.MANAGER.getRoleCode());
    }

    protected static boolean isSuperAdminAndAdmin() {
        return SecurityUtils.isCurrentUserInRoles(Role.SUPER_ADMIN.getRoleCode(), Role.ADMIN.getRoleCode());
    }

    protected static boolean isOperator() {
        return SecurityUtils.isCurrentUserInRole(Role.OPERATOR.getRoleCode());
    }

    @SuppressWarnings("unchecked")
    private Class<T> getEntityClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    private String getTableName() {
        Class<T> entityClass = getEntityClass();
        return entityManager
                .getMetamodel()
                .entity(entityClass)
                .getJavaType()
                .getAnnotation(Table.class)
                .name()
                .toLowerCase();
    }

    private List<String> mapTablesToEntities(List<String[]> tableNames) {
        Metamodel metamodel = entityManager.getMetamodel();
        Set<EntityType<?>> entities = metamodel.getEntities();
        Map<String, String> tableToEntityMap = new HashMap<>();

        for (EntityType<?> entity : entities) {
            Table tableAnnotation = entity.getJavaType().getAnnotation(Table.class);
            if (tableAnnotation != null) {
                tableToEntityMap.put(tableAnnotation.name().toLowerCase(), entity.getJavaType().getSimpleName());
            }
        }

        List<String> entityNames = new ArrayList<>();
        tableNames.forEach(t -> {
            for (String tableName : t) {
                if (tableToEntityMap.containsKey(tableName)) {
                    entityNames.add(tableToEntityMap.get(tableName));
                }
            }
        });

        return entityNames;
    }
}
