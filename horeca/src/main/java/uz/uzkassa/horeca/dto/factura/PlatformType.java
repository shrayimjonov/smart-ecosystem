package uz.uzkassa.horeca.dto.factura;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 03.10.2024
 */

@Getter
@AllArgsConstructor
public enum PlatformType {
    BACKEND("BACKEND"),
    ANDROID("ANDROID"),
    WEB("WEB"),
    IOS("IOS"),
    TERMINAL("TERMINAL");

    private final String name;
}
