package uz.uzkassa.cts.dto.tin;

import java.io.Serializable;

public class TaxRequest implements Serializable {

    private String action;
    private String tin;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }
}
