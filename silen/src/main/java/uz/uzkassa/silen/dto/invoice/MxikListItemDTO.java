package uz.uzkassa.silen.dto.invoice;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MxikListItemDTO {
    private String mxikCode;
    private MxikInfo info;
}
