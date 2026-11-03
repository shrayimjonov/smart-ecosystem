package uz.uzkassa.silen.dto.mq;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Payload implements Serializable {
    private String id;
    private String extraId;
    private Long longId;

    public Payload(String id) {
        this.id = id;
    }

    public Payload(Long longId) {
        this.longId = longId;
    }

    public Payload(String id, Long longId) {
        this.id = id;
        this.longId = longId;
    }

    public Payload(String id, String extraId) {
        this.id = id;
        this.extraId = extraId;
    }
}
