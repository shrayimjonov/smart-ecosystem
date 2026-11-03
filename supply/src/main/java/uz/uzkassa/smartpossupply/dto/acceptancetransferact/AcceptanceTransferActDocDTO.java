package uz.uzkassa.smartpossupply.dto.acceptancetransferact;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AcceptanceTransferActDocDTO implements Serializable {

    @JsonProperty("AcceptanceTransferActNo")
    String acceptanceTransferActNo;

    @JsonProperty("AcceptanceTransferActDate")
    String acceptanceTransferActDate;

    @JsonSetter("acceptancetransferactno")
    public void setacceptancetransferactno(String acceptancetransferactno) {
        this.acceptanceTransferActNo = acceptancetransferactno;
    }

    @JsonSetter("acceptancetransferactdate")
    public void setacceptancetransferactdate(String acceptancetransferactdate) {
        this.acceptanceTransferActDate = acceptancetransferactdate;
    }

    public void setAcceptanceTransferActNo(String acceptanceTransferActNo) {
        this.acceptanceTransferActNo = acceptanceTransferActNo;
    }

    public void setAcceptanceTransferActDate(String acceptanceTransferActDate) {
        this.acceptanceTransferActDate = acceptanceTransferActDate;
    }
}
