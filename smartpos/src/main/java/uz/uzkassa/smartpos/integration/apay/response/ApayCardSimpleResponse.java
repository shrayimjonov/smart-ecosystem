package uz.uzkassa.smartpos.integration.apay.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ApayCardSimpleResponse implements Serializable {
    private String id;
    private String number;
    private String name;
    private String cardBackground;
    private String cardMiniBackground;
    private String bankLogo;
}
