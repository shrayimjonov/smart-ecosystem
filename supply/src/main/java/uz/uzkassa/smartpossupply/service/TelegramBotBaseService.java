package uz.uzkassa.smartpossupply.service;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uz.uzkassa.smartpossupply.config.ApplicationProperties;

import java.io.IOException;

import static lombok.AccessLevel.PROTECTED;

@Component
@Slf4j
@FieldDefaults(level = PROTECTED, makeFinal = true)
@RequiredArgsConstructor
public class TelegramBotBaseService {

    ApplicationProperties applicationProperties;

    public void send(String message, String channelId, TelegramBot telegramBot) {
        if (!StringUtils.hasText(message)) {
            return;
        }
        telegramBot.execute(
                new SendMessage(channelId, message)
                        .parseMode(ParseMode.HTML),
                        //.disableWebPagePreview(true),
                new Callback<SendMessage, SendResponse>() {
                    @Override
                    public void onResponse(SendMessage request, SendResponse response) {
                    }

                    @Override
                    public void onFailure(SendMessage request, IOException e) {
                        log.error("Message send to Telegram channel: {}", e.getMessage());
                    }
                }
        );
    }
}
