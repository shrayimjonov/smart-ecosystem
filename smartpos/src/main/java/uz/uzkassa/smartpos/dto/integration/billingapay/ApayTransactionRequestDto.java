package uz.uzkassa.smartpos.dto.integration.billingapay;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ApayTransactionRequestDto {
    private String tin;
    @NotNull
    private LocalDate from;
    @NotNull
    private LocalDate to;
    private Integer page;
    private Integer size;


    public MultiValueMap<String, String> getMultiValueMap() {
        var map = new LinkedMultiValueMap<String, String>();
        if (tin != null) {
            map.put("tin", List.of(tin));
        }
        if (from != null) {
            map.put("from", List.of(String.valueOf(from)));
        }
        if (to != null) {
            map.put("to", List.of(String.valueOf(to)));
        }
        if (page != null) {
            map.put("page", List.of(String.valueOf(page)));
        }
        if (size != null) {
            map.put("size", List.of(String.valueOf(size)));
        }
        return map;
    }
}
