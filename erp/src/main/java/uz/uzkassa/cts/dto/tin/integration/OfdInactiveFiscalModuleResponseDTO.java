package uz.uzkassa.cts.dto.tin.integration;

import java.util.List;

public class OfdInactiveFiscalModuleResponseDTO extends OfdFullResponseDTO {

    private List<OfdInactiveFiscalModuleDTO> data;

    public List<OfdInactiveFiscalModuleDTO> getData() {
        return data;
    }

    public void setData(List<OfdInactiveFiscalModuleDTO> data) {
        this.data = data;
    }
}
