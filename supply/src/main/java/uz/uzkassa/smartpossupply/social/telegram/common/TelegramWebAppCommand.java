package uz.uzkassa.smartpossupply.social.telegram.common;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.WebAppInfo;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uz.uzkassa.smartpossupply.config.ApplicationProperties;
import uz.uzkassa.smartpossupply.enums.TelegramBotType;

@Component
public class TelegramWebAppCommand {

    ApplicationProperties applicationProperties;

    public TelegramWebAppCommand(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public void startWebAppCommand(Long chatId, Long userId, TelegramBot telegramBot, TelegramBotType  telegramBotType) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder
            .fromUriString(applicationProperties.getServerUrl().concat("/bot"));
        uriBuilder.queryParam("bot_token", telegramBot.getToken());
        uriBuilder.queryParam("telegramUserId", userId);
        uriBuilder.queryParam("botType", telegramBotType.getCode());

        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup(
            new KeyboardButton("Web App")
                .webAppInfo(new WebAppInfo(uriBuilder.build().toUriString()))
        ).resizeKeyboard(true);

        telegramBot.execute(new SendMessage(chatId, "Web App ").replyMarkup(keyboard)
        );
    }
}
