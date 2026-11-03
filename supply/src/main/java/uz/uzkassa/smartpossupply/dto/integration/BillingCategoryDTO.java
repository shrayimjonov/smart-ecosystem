package uz.uzkassa.smartpossupply.dto.integration;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class BillingCategoryDTO implements Serializable {

    private String code;
    private String nameRu;
    private String nameUzCyrillic;
    private String nameUzLatin;
    private Boolean selectable;
}
