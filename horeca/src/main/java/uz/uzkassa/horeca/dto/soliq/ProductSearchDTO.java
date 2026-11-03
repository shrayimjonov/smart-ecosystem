package uz.uzkassa.horeca.dto.soliq;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductSearchDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 34387686L;

    String text;

    String lang;

    Integer page=0;

    Integer size=10;

    String mxikCode;

    String gtin;

    String tnvedCode;

    String brandName;

    String markCode;

    Long branchId;
}
