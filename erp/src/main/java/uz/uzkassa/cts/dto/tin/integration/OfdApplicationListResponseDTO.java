package uz.uzkassa.cts.dto.tin.integration;


import java.util.List;

public class OfdApplicationListResponseDTO extends OfdFullResponseDTO {

    private List<OfdApplicationDTO> data;

    public List<OfdApplicationDTO> getData() {
        return data;
    }

    public void setData(List<OfdApplicationDTO> data) {
        this.data = data;
    }
}
