package uz.uzkassa.smartpossupply.dto.base;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PagedResponseDTO<T> implements Serializable {

    Long totalElements;

    Long totalPages;

    Long numberOfElements;

    Long size;

    Long number;

    List<T> content = new ArrayList<>();
}
