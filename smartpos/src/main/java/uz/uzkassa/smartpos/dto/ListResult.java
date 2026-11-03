package uz.uzkassa.smartpos.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ListResult<T> implements Serializable {
    private List<T> items;
    private Long total;
}
