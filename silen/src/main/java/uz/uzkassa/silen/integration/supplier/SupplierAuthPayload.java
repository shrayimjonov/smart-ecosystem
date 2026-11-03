package uz.uzkassa.silen.integration.supplier;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * {"companyType":"SUPPLIER","rememberMe":true,"password":"","phone":"998901234567"}
 */
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SupplierAuthPayload {

    boolean rememberMe = true;

    String username;

    String password;

    public SupplierAuthPayload(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
