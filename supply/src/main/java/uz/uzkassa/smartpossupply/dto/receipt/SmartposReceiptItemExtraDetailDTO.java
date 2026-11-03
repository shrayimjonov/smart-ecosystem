package uz.uzkassa.smartpossupply.dto.receipt;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import uz.uzkassa.smartpossupply.domain.product.Product;
import uz.uzkassa.smartpossupply.domain.stock.StockProduct;

import java.io.Serializable;

/**
 * Created by: Shuxratjon Rayimjonov
 * Date: 14.08.2024
 */

@Getter
@Setter
@ToString
public class SmartposReceiptItemExtraDetailDTO implements Serializable {

    private String packageCode;
    private String packageName;

    public SmartposReceiptItemExtraDetailDTO(StockProduct stockProduct) {
        this.packageCode = stockProduct.getPackageCode();
        this.packageName = stockProduct.getPackageName();
    }

    public SmartposReceiptItemExtraDetailDTO(Product product) {
        this.packageCode = product.getPackageCode();
        this.packageName = product.getPackageName();
    }

    public SmartposReceiptItemExtraDetailDTO(String packageCode, String packageName) {
        this.packageCode = packageCode;
        this.packageName = packageName;
    }
}
