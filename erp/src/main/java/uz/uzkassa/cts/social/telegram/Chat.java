package uz.uzkassa.cts.social.telegram;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class Chat implements Serializable {
    private String id;
    private String first_name;
    private String last_name;
    private String username;
    private String type;
}
