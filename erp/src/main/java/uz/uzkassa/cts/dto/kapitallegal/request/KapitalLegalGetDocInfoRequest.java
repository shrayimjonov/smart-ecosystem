package uz.uzkassa.cts.dto.kapitallegal.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KapitalLegalGetDocInfoRequest {
    private String sid;
    private String branch;
    private String account;
}
