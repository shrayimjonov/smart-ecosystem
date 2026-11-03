package uz.uzkassa.silen.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ListResult<T> implements Serializable {
    private List<T> items;
    private Long total;
}
