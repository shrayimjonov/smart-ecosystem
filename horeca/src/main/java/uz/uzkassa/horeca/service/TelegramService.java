package uz.uzkassa.horeca.service;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uz.uzkassa.horeca.config.ApplicationProperties;

import java.io.IOException;

@Component
@Slf4j
public class TelegramService {

    TelegramBot telegramBot;
    @Autowired
    ApplicationProperties applicationProperties;

    @PostConstruct
    public void init() {
        telegramBot = new com.pengrad.telegrambot.TelegramBot.Builder(applicationProperties.getTelegramBotConfig().getToken())
                .apiUrl("http://127.0.0.1:8080/bot")
                .build();
    }

    @PreDestroy
    private void clear() {
        telegramBot = null;
    }

    public void send(String message) {
        if (!StringUtils.hasText(message)) {
            return;
        }
//        telegramBot.execute(
//                new SendMessage(applicationProperties.getTelegramBotConfig().getChannelId(), message)
//                        .parseMode(ParseMode.HTML)
//                        .disableWebPagePreview(true),
//                new Callback<SendMessage, SendResponse>() {
//                    @Override
//                    public void onResponse(com.pengrad.telegrambot.request.SendMessage request, SendResponse response) {
//                        //log.info("Message send to Telegram channel: {}", response.toString());
//                    }
//
//                    @Override
//                    public void onFailure(com.pengrad.telegrambot.request.SendMessage request, IOException e) {
//                        log.error("Message send to Telegram channel: {}", e.getMessage());
//                    }
//                }
//        );
    }
}
