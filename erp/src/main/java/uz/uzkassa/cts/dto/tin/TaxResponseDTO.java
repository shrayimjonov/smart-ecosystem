package uz.uzkassa.cts.dto.tin;

import java.util.List;

public class TaxResponseDTO {

    private Integer err_code;
    private String err_text;
    private List<TaxResponse> root;

    public Integer getErr_code() {
        return err_code;
    }

    public void setErr_code(Integer err_code) {
        this.err_code = err_code;
    }

    public String getErr_text() {
        return err_text;
    }

    public void setErr_text(String err_text) {
        this.err_text = err_text;
    }

    public List<TaxResponse> getRoot() {
        return root;
    }

    public void setRoot(List<TaxResponse> root) {
        this.root = root;
    }

    @Override
    public String toString() {
        return "TaxResTemplate{" +
                "err_code=" + err_code +
                ", err_text='" + err_text + '\'' +
                ", root=" + root +
                '}';
    }
}
