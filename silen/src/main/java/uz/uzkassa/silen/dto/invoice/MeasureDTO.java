package uz.uzkassa.silen.dto.invoice;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MeasureDTO implements Serializable {
    private Long measureId;
    private String name;
}
