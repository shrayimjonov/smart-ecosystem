package uz.uzkassa.silen.dto.marking;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.silen.enumeration.PackageType;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class CodeDTO implements Serializable {

    private String code;

    private String productId;

    private PackageType packageType;

}
