package uz.uzkassa.cts.social.telegram;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Location implements Serializable {
    private Double latitude;
    private Double longitude;
}
