package uz.uzkassa.cts.repository.impl;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import uz.uzkassa.cts.enums.Role;
import uz.uzkassa.cts.security.SecurityUtils;
import uz.uzkassa.cts.service.JdbcSpringManager;

import java.util.Optional;

public abstract class BaseRepositoryImpl {

    @Autowired
    protected JdbcSpringManager jdbcSpringManager;

    @PersistenceContext
    protected EntityManager entityManager;


    protected static Long getCurrentUserId() {
        return SecurityUtils.getCurrentUserId();
    }

    protected static Long getCompanyId() {
        return SecurityUtils.getCompanyId();
    }

    protected static Long getCompanyId(Long companyId) {
        return Optional.ofNullable(companyId).orElse(getCompanyId());
    }

    protected static boolean isSuperAdmin() {
        return SecurityUtils.isCurrentUserInRole(Role.SUPER_ADMIN.getRoleCode());
    }

    protected static boolean isSuperAdminAndAdmin() {
        return SecurityUtils.isCurrentUserInRoles(Role.SUPER_ADMIN.getRoleCode(),Role.ADMIN.getRoleCode());
    }
}
