package uz.uzkassa.cts.social.telegram;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class InlineQuery implements Serializable {
    private String id;
    private From from;
    private String query;
    private String offset;
}
