package uz.uzkassa.smartpos.security;

import java.util.Set;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {
    public static final String USER = "ROLE_USER"; //Warning not to use in APIs
    public static final String ADMIN = "ROLE_ADMIN"; //Warning do not apply to users
    public static final String SMARTPOS_ADMIN = "ROLE_SMARTPOS_ADMIN";
    public static final String OWNER = "ROLE_OWNER";
    public static final String BRANCH_ADMIN = "ROLE_BRANCH_ADMIN";
    public static final String CASHIER = "ROLE_CASHIER";
    public static final String SALES_MANAGER = "ROLE_SALES_MANAGER";
    public static final String SUPPORT_MANAGER = "ROLE_SUPPORT_MANAGER";
    public static final String REGION_MANAGER = "ROLE_REGION_MANAGER";
    public static final String SALES_DIRECTOR = "ROLE_SALES_DIRECTOR";
    public static final String SUPPORT = "ROLE_SUPPORT";

    public static final Set<String> ADMIN_ROLES = Set.of(ADMIN, SMARTPOS_ADMIN, SALES_MANAGER, SUPPORT_MANAGER, REGION_MANAGER, SALES_DIRECTOR, SUPPORT);
    public static final Set<String> ADMIN_AND_OWNER_ROLES = Set.of(SMARTPOS_ADMIN, SUPPORT, SUPPORT_MANAGER, SALES_MANAGER, REGION_MANAGER, SALES_DIRECTOR, OWNER);
    public static final Set<String> ADMIN_AND_BRANCH_ADMIN_ROLES = Set.of(SMARTPOS_ADMIN, SUPPORT, SUPPORT_MANAGER, SALES_MANAGER, REGION_MANAGER, SALES_DIRECTOR, OWNER, BRANCH_ADMIN);
}
