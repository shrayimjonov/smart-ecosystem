package uz.uzkassa.silen.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FacturaHostDTO {

    private Long id;

    private String host;

    private boolean active;
}
