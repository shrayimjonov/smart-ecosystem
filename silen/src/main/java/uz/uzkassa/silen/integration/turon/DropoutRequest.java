package uz.uzkassa.silen.integration.turon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DropoutRequest implements Serializable {
    private Set<String> sntins;
    private String participantId;
    private String address;
    private String dropoutReason;
    private boolean withChild;
}
