package uz.uzkassa.cts.dto.kapitallegal.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class KapitalLegalClient {
    private Long id;
    private String branch;
    private String code;
    private String name;
    @JsonProperty("oper_day")
    private String operDay;
    private String inn;
    @JsonProperty("dir_name")
    private String dirName;
    @JsonProperty("buh_name")
    private String buhName;
    private String address;
    private String phone;
    private String fax;
    private List<KapitalLegalAccount> accounts;
}
