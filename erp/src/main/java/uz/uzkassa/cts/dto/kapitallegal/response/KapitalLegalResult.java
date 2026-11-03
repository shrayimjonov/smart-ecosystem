package uz.uzkassa.cts.dto.kapitallegal.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class KapitalLegalResult {
    private String login;
    private String sid;
    private List<KapitalLegalClient> clients;
}
