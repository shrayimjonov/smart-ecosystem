package uz.uzkassa.cts.dto.categoryfield;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryFieldBatchDTO implements Serializable {

    Long categoryId;

    List<Long> fieldIds;
}
