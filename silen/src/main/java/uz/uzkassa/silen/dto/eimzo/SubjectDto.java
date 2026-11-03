package uz.uzkassa.silen.dto.eimzo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Optional;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubjectDto implements Serializable {
    @JsonProperty("C")
    private String country;
    @JsonProperty("ST")
    private String city;
    @JsonProperty("L")
    private String district;
    @JsonProperty("UID")
    private String uid;
    @JsonProperty("O")
    private String companyName;
    @JsonProperty("SURNAME")
    private String lastName;
    @JsonProperty("NAME")
    private String firstName;
    @JsonProperty("T")
    private String position;
    @JsonProperty("BusinessCategory")
    private String businessCategory;
    @JsonProperty("1.2.860.3.16.1.1")
    private String tin;
    @JsonProperty("1.2.860.3.16.1.2")
    private String pinFl;
    @JsonProperty("CN")
    private String fullName;

    public String getTinOrPinFl() {
        return Optional.ofNullable(this.tin).orElse(this.pinFl);
    }
}
