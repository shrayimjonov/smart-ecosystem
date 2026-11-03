package uz.uzkassa.silen.dto.invoice;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class MxikListDTO {
    private List<MxikListItemDTO> data;
}
