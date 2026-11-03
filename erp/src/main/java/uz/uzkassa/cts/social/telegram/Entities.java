package uz.uzkassa.cts.social.telegram;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class Entities implements Serializable {
    private Long offset;
    private Long length;
    private String type;
}
