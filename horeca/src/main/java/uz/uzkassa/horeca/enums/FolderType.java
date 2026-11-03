package uz.uzkassa.horeca.enums;

import java.io.Serializable;

public enum FolderType implements Serializable {
    PUBLIC,
    PRIVATE;

    public static FolderType get(String folderType) {
        return FolderType.valueOf(folderType);
    }
}
