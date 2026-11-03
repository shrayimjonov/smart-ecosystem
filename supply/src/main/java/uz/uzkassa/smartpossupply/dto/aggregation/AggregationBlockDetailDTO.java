package uz.uzkassa.smartpossupply.dto.aggregation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;
import uz.uzkassa.smartpossupply.enums.PackageType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AggregationBlockDetailDTO implements Serializable {

    CommonDTO product;

    Integer qty;

    String code;

    String printCode;

    String description;

    String reportId;

    PackageType packageType;

    CommonDTO childProduct;

    List<String> marks = new ArrayList<>();

    @JsonIgnore
    public void addMark(String mark) {
        marks.add(mark);
    }
}
