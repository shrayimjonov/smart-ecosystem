package uz.uzkassa.cts.dto.tin;

public class TaxResponse {

    private String nomer_reg;
    private String sost;
    private Integer ns10_code;
    private String address;
    private Integer ns11_code;
    private String date_reg;
    private String aname;

    public String getNomer_reg() {
        return nomer_reg;
    }

    public void setNomer_reg(String nomer_reg) {
        this.nomer_reg = nomer_reg;
    }

    public String getSost() {
        return sost;
    }

    public void setSost(String sost) {
        this.sost = sost;
    }

    public Integer getNs10_code() {
        return ns10_code;
    }

    public void setNs10_code(Integer ns10_code) {
        this.ns10_code = ns10_code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getNs11_code() {
        return ns11_code;
    }

    public void setNs11_code(Integer ns11_code) {
        this.ns11_code = ns11_code;
    }

    public String getDate_reg() {
        return date_reg;
    }

    public void setDate_reg(String date_reg) {
        this.date_reg = date_reg;
    }

    public String getAname() {
        return aname;
    }

    public void setAname(String aname) {
        this.aname = aname;
    }

    @Override
    public String toString() {
        return "TaxResponse{" +
                "nomer_reg=" + nomer_reg +
                ", sost='" + sost + '\'' +
                ", ns10_code=" + ns10_code +
                ", address='" + address + '\'' +
                ", ns11_code=" + ns11_code +
                ", date_reg='" + date_reg + '\'' +
                ", aname='" + aname + '\'' +
                '}';
    }
}
