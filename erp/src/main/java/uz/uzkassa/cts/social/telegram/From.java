package uz.uzkassa.cts.social.telegram;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class From implements Serializable {
    private Long id;
    private boolean is_bot;
    private String first_name;
    private String last_name;
    private String username;
    private String language_code;
}
