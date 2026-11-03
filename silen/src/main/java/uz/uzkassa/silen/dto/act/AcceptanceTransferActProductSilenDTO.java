package uz.uzkassa.silen.dto.act;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.silen.dto.SelectItem;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcceptanceTransferActProductSilenDTO {

    String id;

    String serialId;

    SelectItem serial;

    String productId;

    ActProductItemDTO product;

    Integer quantity;

    BigDecimal price;

    BigDecimal cost;

    BigDecimal discount;

    BigDecimal paymentSum;

    Set<String> aggregations;

    Set<String> blocks;

    Set<String> marks;

    public void addAggregation(String printCode) {
        if (this.aggregations == null) {
            this.aggregations = new HashSet<>();
        }
        this.aggregations.add(printCode);
    }

    public void addBlock(String printCode) {
        if (this.blocks == null) {
            this.blocks = new HashSet<>();
        }
        this.blocks.add(printCode);
    }

    public void addMark(String printCode) {
        if (this.marks == null) {
            this.marks = new HashSet<>();
        }
        this.marks.add(printCode);
    }
}
