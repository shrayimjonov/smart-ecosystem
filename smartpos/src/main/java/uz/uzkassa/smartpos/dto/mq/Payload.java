package uz.uzkassa.smartpos.dto.mq;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Payload implements Serializable {
    private Long id;
    private String extra;

    public Payload(Long id) {
        this.id = id;
    }
}
