package uz.uzkassa.smartpossupply.dto.integration;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CategoryDetailResponseDto implements Serializable {

    private String id;

    private String code;

    private String nameRu;

    private String nameUzCyrillic;

    private String nameUzLatin;

    private Boolean selectable = Boolean.TRUE;

    private Boolean active = Boolean.TRUE;

    private Boolean recurring;

    private String parentId;
}
