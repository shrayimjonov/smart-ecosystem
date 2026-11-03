package uz.uzkassa.cts.social.telegram;

import com.pengrad.telegrambot.model.request.Keyboard;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@ToString
@AllArgsConstructor
public class MessageSend {

    private final Long chatId;
    private final String text;
    private final Keyboard keyboard;

    public MessageSend(Long chatId, String text) {
        this.chatId = chatId;
        this.text = text;
        this.keyboard = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageSend that = (MessageSend) o;
        return Objects.equals(chatId, that.chatId) &&
                Objects.equals(text, that.text) &&
                Objects.equals(keyboard, that.keyboard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, text, keyboard);
    }
}

