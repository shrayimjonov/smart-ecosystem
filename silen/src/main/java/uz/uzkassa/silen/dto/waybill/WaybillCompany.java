package uz.uzkassa.silen.dto.waybill;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WaybillCompany implements Serializable {
    @JsonProperty("TinOrPinfl")
    private String tinOrPinfl;
    @JsonProperty("Name")
    private String name;
    @JsonProperty("BranchCode")
    private String branchCode;
    @JsonProperty("BranchName")
    private String branchName;

    @JsonSetter("tinorpinfl")
    public void settinorpinflLowerCase(String tinorpinfl) {
        this.tinOrPinfl = tinorpinfl;
    }

    @JsonSetter("name")
    public void setnameLowerCase(String name) {
        this.name = name;
    }

    @JsonSetter("branchcode")
    public void setbranchcodeLowerCase(String branchcode) {
        this.branchCode = branchcode;
    }

    @JsonSetter("branchname")
    public void setbranchnameLowerCase(String branchname) {
        this.branchName = branchname;
    }
}
