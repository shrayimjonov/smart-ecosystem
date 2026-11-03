package uz.uzkassa.cts.dto.apay.category;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class APayCategoryListDTO {

    String id;

    String parentId;

    String nameRu;

    String nameCyrillic;

    String nameUz;
}
