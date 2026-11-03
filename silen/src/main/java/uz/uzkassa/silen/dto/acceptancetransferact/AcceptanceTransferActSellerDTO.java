package uz.uzkassa.silen.dto.acceptancetransferact;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AcceptanceTransferActSellerDTO implements Serializable {

    @JsonProperty("Name")
    protected String name;

    @JsonProperty("Address")
    protected String address;

    @JsonProperty("Account")
    protected String account;

    @JsonProperty("BankId")
    protected String bankId;

    @JsonSetter("name")
    public void setNameLowerCase(String name) {
        this.name = name;
    }

    @JsonSetter("address")
    public void setAddressLowerCase(String address) {
        this.address = address;
    }

    @JsonSetter("account")
    public void setAccountLowerCase(String account) {
        this.account = account;
    }

    @JsonSetter("bankid")
    public void setbankid(String bankid) {
        this.bankId = bankid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }
}
