package uz.uzkassa.cts.social.telegram;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class CallbackQuery implements Serializable {
    private Long id;
    private From from;
    private Message message;
    private String data;
}
