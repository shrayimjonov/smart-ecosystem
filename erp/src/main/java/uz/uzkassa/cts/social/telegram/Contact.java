package uz.uzkassa.cts.social.telegram;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class Contact implements Serializable {
    private String phone_number;
    private String first_name;
    private String last_name;
    private Long user_id;
}
