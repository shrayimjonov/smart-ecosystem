package uz.uzkassa.cts.dto.tin.integration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OfdAccountDTO implements Serializable {

    private String tin;
    private String pinfl;
    private BigDecimal overpaymentSum;
    private BigDecimal terminalCost;
    private Integer unpaidCount;

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public String getPinfl() {
        return pinfl;
    }

    public void setPinfl(String pinfl) {
        this.pinfl = pinfl;
    }

    public BigDecimal getOverpaymentSum() {
        return overpaymentSum;
    }

    public void setOverpaymentSum(BigDecimal overpaymentSum) {
        this.overpaymentSum = overpaymentSum;
    }

    public BigDecimal getTerminalCost() {
        return terminalCost;
    }

    public void setTerminalCost(BigDecimal terminalCost) {
        this.terminalCost = terminalCost;
    }

    public Integer getUnpaidCount() {
        return unpaidCount;
    }

    public void setUnpaidCount(Integer unpaidCount) {
        this.unpaidCount = unpaidCount;
    }

}
