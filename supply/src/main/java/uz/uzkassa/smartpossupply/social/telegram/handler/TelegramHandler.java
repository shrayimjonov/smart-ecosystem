package uz.uzkassa.smartpossupply.social.telegram.handler;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.stereotype.Service;

@Service
public interface TelegramHandler<T> {
    void handle(T t, TelegramBot telegramBot);
}
