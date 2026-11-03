package uz.uzkassa.silen.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrintAggregationDTO implements Serializable {

    private String aggregationCode;

    private Set<PrintAggregationMarkDTO> marks;
}
