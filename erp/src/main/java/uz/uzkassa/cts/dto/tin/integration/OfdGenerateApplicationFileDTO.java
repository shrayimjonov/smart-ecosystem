package uz.uzkassa.cts.dto.tin.integration;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.ToString;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@ToString
public class OfdGenerateApplicationFileDTO implements Serializable {

    private String name;
    private String extension;
    private String sourceBase64;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getSourceBase64() {
        return sourceBase64;
    }

    public void setSourceBase64(String sourceBase64) {
        this.sourceBase64 = sourceBase64;
    }
}
