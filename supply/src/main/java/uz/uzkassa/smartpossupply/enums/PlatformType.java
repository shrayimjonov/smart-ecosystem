package uz.uzkassa.smartpossupply.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 26.11.2024
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
