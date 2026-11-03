package uz.uzkassa.smartpos.dto.elasticsearch;

import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import uz.uzkassa.smartpos.dto.receipt.ReceiptDetailDTO;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class ReceiptDetailES implements Serializable {
    private Long id;

    private Long productId;

    private String productName;

    private String productBarcode;

    private String vatBarcode;

    private String committentTin;

    private Long unitId;

    private String unitName;

    private Long categoryId;

    private String categoryName;

    private BigDecimal price;

    private BigDecimal qty;

    private BigDecimal amount;

    private BigDecimal nds;

    private BigDecimal ndsPercent;

    private BigDecimal discount;

    private BigDecimal discountPercent;

    private BigDecimal excise;

    private BigDecimal exciseRate;


    @Transient
    public ReceiptDetailDTO getDTO() {
        ReceiptDetailDTO receiptDetailDTO = new ReceiptDetailDTO();

        receiptDetailDTO.setProductId(getProductId());
        receiptDetailDTO.setProductName(getProductName());
        if (StringUtils.isNotEmpty(getProductBarcode())) {
            if (getProductBarcode().contains("$")) {
                String[] barcodes = getProductBarcode().split("\\$");
                receiptDetailDTO.setProductBarcode(barcodes[0]);
            } else {
                receiptDetailDTO.setProductBarcode(getProductBarcode());
            }
        }
        if (StringUtils.isNotEmpty(getVatBarcode())) {
            receiptDetailDTO.setVatBarcode(getVatBarcode());
        } else if (StringUtils.isNotEmpty(getProductBarcode()) && getProductBarcode().contains("$")) {
            String[] barcodes = getProductBarcode().split("\\$");
            if (barcodes.length > 1) {
                receiptDetailDTO.setVatBarcode(barcodes[1]);
            }
        }
        receiptDetailDTO.setCommittentTin(getCommittentTin());

        receiptDetailDTO.setUnitId(getUnitId());
        receiptDetailDTO.setUnitName(getUnitName());

        receiptDetailDTO.setCategoryId(getCategoryId());
        receiptDetailDTO.setCategoryName(getCategoryName());

        receiptDetailDTO.setPrice(getPrice());
        receiptDetailDTO.setQty(getQty());
        receiptDetailDTO.setAmount(getAmount());

        receiptDetailDTO.setNdsPercent(getNdsPercent());
        receiptDetailDTO.setNds(getNds());
        receiptDetailDTO.setDiscount(getDiscount());
        receiptDetailDTO.setExcise(getExcise());
        receiptDetailDTO.setExciseRate(getExciseRate());
        return receiptDetailDTO;
    }
}
