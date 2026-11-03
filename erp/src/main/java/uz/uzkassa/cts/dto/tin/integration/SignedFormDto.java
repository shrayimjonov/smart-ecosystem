package uz.uzkassa.cts.dto.tin.integration;

public class SignedFormDto {

    private String certificateId;
    private String lang;
    private String xmlSign;

    public String getCertificateId() {
        return certificateId;
    }

    public void setCertificateId(String certificateId) {
        this.certificateId = certificateId;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getXmlSign() {
        return xmlSign;
    }

    public void setXmlSign(String xmlSign) {
        this.xmlSign = xmlSign;
    }
}
