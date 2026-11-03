package uz.uzkassa.smartpossupply.dto.aic;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.AicAdditionalUnit;
import uz.uzkassa.smartpossupply.enums.ProductGroup;
import uz.uzkassa.smartpossupply.enums.ProductType;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AicProductLookUpDTO extends CommonDTO {

    String barcode;

    ProductType productType;

    String tnVed;

    String catalogCode;

    String catalogName;

    BigDecimal capacity;

    BigDecimal strengthen;

    String packageCode;

    String packageName;

    AicAdditionalUnit additionalUnit;

    public AicProductLookUpDTO(Long id, String name, String barcode, String tnVed) {
        super(id, name);
        this.barcode = barcode;
        this.tnVed = tnVed;
    }

    public AicProductLookUpDTO(Long id, String name, String barcode, String tnVed, ProductType productType, String catalogCode, String catalogName, String packageCode, String packageName, AicAdditionalUnit additionalUnit) {
        super(id, name);
        this.barcode = barcode;
        this.tnVed = tnVed;
        this.productType = productType;
        this.catalogCode = catalogCode;
        this.catalogName = catalogName;
        if (productType != null) {
            if (ProductGroup.Alcohol.equals(productType.getProductGroup())) {

                if (StringUtils.isNotEmpty(name)) {
                    String regex = "(\\d+[.,]?\\d*)\\s*%";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(name);
                    if (matcher.find()) {
                        String number = matcher.group(1);
                        if (StringUtils.isNotEmpty(number)){
                            number=number.replace(",",".");
                        }
                        strengthen = new BigDecimal(number);
                    }
                }
            }
            if (ProductGroup.Alcohol.equals(productType.getProductGroup()) || ProductGroup.Beer.equals(productType.getProductGroup()) || ProductGroup.Water.equals(productType.getProductGroup())) {
                if (StringUtils.isNotEmpty(packageName)) {
                    String regex = "(\\d+\\.?\\d*)";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(packageName);
                    if (matcher.find()) {
                        String number = matcher.group(1);
                        capacity = new BigDecimal(number);
                    }
                }
            }
        }
        this.packageCode = packageCode;
        this.packageName = packageName;
        this.additionalUnit = additionalUnit;
    }
}