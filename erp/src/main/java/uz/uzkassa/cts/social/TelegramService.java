package uz.uzkassa.cts.social;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uz.uzkassa.cts.config.ApplicationProperties;
import uz.uzkassa.cts.social.telegram.MessageSend;
import uz.uzkassa.cts.web.rest.errors.IntegrationException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramService {
    private final ApplicationProperties applicationProperties;
    private final TelegramBot telegramBot;

    public void sendMessage(MessageSend message) {

        SendMessage request = new SendMessage(message.getChatId(), message.getText());
        request.parseMode(ParseMode.HTML);
        if (Objects.nonNull(message.getKeyboard())) {
            request.replyMarkup(message.getKeyboard());
        } else {
            request.replyMarkup(new ReplyKeyboardRemove(true));
        }
        telegramBot.execute(request, new Callback<SendMessage, SendResponse>() {
            @Override
            public void onResponse(com.pengrad.telegrambot.request.SendMessage sendMessage, SendResponse sendResponse) {
            }

            @Override
            public void onFailure(com.pengrad.telegrambot.request.SendMessage sendMessage, IOException e) {
                log.error("Error sending message to Telegram channel: {}", e.getMessage());
            }
        });
    }

    public void sendMessage(String message, String id) {
        if (!StringUtils.hasText(message)) {
            return;
        }
        if (StringUtils.hasText(id)) {
            SendMessage sendMessage = new SendMessage(id, message);
            sendMessage.disableWebPagePreview(true);
            sendMessage.parseMode(ParseMode.HTML);
            telegramBot.execute(sendMessage, new Callback<SendMessage, SendResponse>() {
                @Override
                public void onResponse(SendMessage sendMessage, SendResponse sendResponse) {
                }

                @Override
                public void onFailure(SendMessage sendMessage, IOException e) {
                    log.error("############# Case message send to Telegram channel: {} ##############", e.getMessage());
                }
            });
        } else {
            this.sendMessageToChannel(message);
        }
    }

    public void sendMessageToChannel(String message) {
        if (!StringUtils.hasText(message)) {
            return;
        }
        telegramBot.execute(new SendMessage(
                        applicationProperties.getTelegramBotProperty().getChannelId(),
                        message
                )
                        .parseMode(ParseMode.HTML)
                        .disableWebPagePreview(true),
                new Callback<com.pengrad.telegrambot.request.SendMessage, SendResponse>() {
                    @Override
                    public void onResponse(com.pengrad.telegrambot.request.SendMessage request, SendResponse response) {
                    }

                    @Override
                    public void onFailure(com.pengrad.telegrambot.request.SendMessage request, IOException e) {
                        log.error("############# Case message send to Telegram channel: {} ##############", e.getMessage());
                    }
                }
        );
    }

    public void checkConnection() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("127.0.0.1", 8080), 30000);
        } catch (Exception e) {
            throw new IntegrationException("An unexpected error has occurred while authenticating to Telegram service. Error: " + e.getMessage());
        }
    }
}
