package uz.uzkassa.silen.dto.filter;

import lombok.Getter;
import lombok.Setter;
import uz.uzkassa.silen.enumeration.DailyOperationTypes;
import uz.uzkassa.silen.enumeration.PackageType;

import java.io.Serializable;

@Getter
@Setter
public class TopFilter extends Filter implements Serializable {

    private String productId;

    private PackageType unit;

    private DailyOperationTypes type;
}
