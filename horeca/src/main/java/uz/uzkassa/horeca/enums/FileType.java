package uz.uzkassa.horeca.enums;

import java.io.Serializable;

public enum FileType implements Serializable {
    COMPANY_BACKGROUND_PHOTO,
    COMPANY_LOGO,
    PRODUCT_PHOTO,
    USER_PROFILE_PHOTO;

    public static FileType get(String folderType) {
        return FileType.valueOf(folderType);
    }
}
