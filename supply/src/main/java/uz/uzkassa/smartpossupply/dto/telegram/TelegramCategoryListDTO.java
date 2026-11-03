package uz.uzkassa.smartpossupply.dto.telegram;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import uz.uzkassa.smartpossupply.dto.base.CommonDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramCategoryListDTO implements Serializable {

    CommonDTO category;

    List<TelegramProductListDTO> products = new ArrayList<>();

    @JsonIgnore
    public void addProduct(TelegramProductListDTO product) {
        products.add(product);
    }
}
