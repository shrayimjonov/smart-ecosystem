package uz.uzkassa.cts.dto.ofd;

import java.io.Serializable;


public class OfdFileDTO implements Serializable {

    private String name;
    private String source;

    public OfdFileDTO() {
    }

    public OfdFileDTO(String name, String source) {
        this.name = name;
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
