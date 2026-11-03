package uz.uzkassa.smartpos.integration.supply.filter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CommentFilter {
    private Long id;
    private Integer page;
    private Integer size;
    private LocalDateTime from;
    private LocalDateTime to;

    public MultiValueMap<String, String> getMultiValueMap() {
        var map = new LinkedMultiValueMap<String, String>();
        if (id != null) {
            map.put("id", List.of(String.valueOf(id)));
        }
        if (page != null) {
            map.put("page", List.of(String.valueOf(page)));
        }
        if (size != null) {
            map.put("size", List.of(String.valueOf(size)));
        }
        if (from != null) {
            map.put("from", List.of(String.valueOf(from)));
        }
        if (to != null) {
            map.put("to", List.of(String.valueOf(to)));
        }
        return map;
    }
}
