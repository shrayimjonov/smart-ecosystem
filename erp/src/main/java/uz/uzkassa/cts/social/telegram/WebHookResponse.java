package uz.uzkassa.cts.social.telegram;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class WebHookResponse implements Serializable {
    private Long update_id;
    private Message message;
    private Message channel_post;
    private InlineQuery inlineQuery;
    private Contact contact;
    private CallbackQuery callback_query;
}
