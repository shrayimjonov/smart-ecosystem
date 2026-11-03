package uz.uzkassa.silen.integration.turon;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class GetMarksResponse implements Serializable {
    private Set<String> codes;
    private String blockId;
}
