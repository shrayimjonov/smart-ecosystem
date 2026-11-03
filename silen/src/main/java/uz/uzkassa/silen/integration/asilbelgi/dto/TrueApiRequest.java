package uz.uzkassa.silen.integration.asilbelgi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrueApiRequest implements Serializable {
    private String organizationId;
    private String code;
    private Set<String> codes;
}
