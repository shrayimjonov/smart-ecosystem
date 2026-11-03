package uz.uzkassa.smartpos.web.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.uzkassa.smartpos.enums.NotificationType;
import uz.uzkassa.smartpos.enums.Role;
import uz.uzkassa.smartpos.enums.TransactionOperation;
import uz.uzkassa.smartpos.enums.sales.SalesType;

import java.util.EnumSet;

@RestController
@RequestMapping("/api/public/enums")
public class EnumResource {
    @GetMapping("/notification-types")
    public NotificationType[] getNotificationTypes() {
        return NotificationType.values();
    }

    @GetMapping("/users/roles")
    public EnumSet<Role> getUserRoles() {
        return EnumSet.of(Role.BRANCH_ADMIN, Role.CASHIER, Role.OWNER);
    }

    @GetMapping("/admin/roles")
    public EnumSet<Role> getAdmin() {
        return EnumSet.of(Role.SUPPORT, Role.SALES_MANAGER, Role.SUPPORT_MANAGER, Role.REGION_MANAGER, Role.SALES_DIRECTOR, Role.SMARTPOS_ADMIN, Role.ADMIN);
    }

    @GetMapping("/all/roles")
    public Role[] getAllRoles() {
        return Role.values();
    }


    @GetMapping("/sales-types")
    public SalesType[] getSalesTypes() {
        return SalesType.values();
    }

    @GetMapping("/transaction-operations")
    public TransactionOperation[] getTransactionOperations() {
        return TransactionOperation.values();
    }
}
