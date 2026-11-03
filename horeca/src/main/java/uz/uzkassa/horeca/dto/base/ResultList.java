package uz.uzkassa.horeca.dto.base;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResultList<T> implements Serializable {

    List<T> list = new ArrayList<>();

    Long count = 0L;

    public ResultList(List<T> list, Long count) {
        this.list = list;
        this.count = count;
    }
}
