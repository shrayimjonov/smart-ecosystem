package uz.uzkassa.smartpossupply.dto.product;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductPhotoDTO implements Serializable {

    Long mainPhotoId;

    List<Long> deletedPhotoIds;

    List<Long> addedPhotoIds;

    boolean applyToAllTheSameProducts;

    List<Long> branchIds = new ArrayList<>();
}
