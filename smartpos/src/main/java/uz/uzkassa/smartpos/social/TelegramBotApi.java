package uz.uzkassa.smartpos.social;

import com.pengrad.telegrambot.Callback;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.LinkPreviewOptions;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.EditMessageText;
import com.pengrad.telegrambot.request.ForwardMessage;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpos.config.ApplicationProperties;

import java.io.IOException;

@Component
@Lazy
@Slf4j
public class TelegramBotApi {
    private final TelegramBot clientBot;
    private final TelegramBot logBot;
    private final ApplicationProperties applicationProperties;

    public TelegramBotApi(@Qualifier("clientBot") TelegramBot clientBot, @Qualifier("logBot") TelegramBot logBot, ApplicationProperties applicationProperties) {
        this.clientBot = clientBot;
        this.logBot = logBot;
        this.applicationProperties = applicationProperties;
    }

    public void sendMessage(SendMessage sendMessage) {
        sendMessage(sendMessage, null);
    }

    public void sendMessage(SendMessage sendMessage, SendMessage queuedMessage) {
        sendMessage(sendMessage, queuedMessage, null);
    }

    public void sendMessage(SendMessage sendMessage, SendMessage queuedMessage, ForwardMessage forwardMessage) {
        clientBot.execute(sendMessage, new Callback<SendMessage, SendResponse>() {
            @Override
            public void onResponse(SendMessage request, SendResponse response) {
//                log.info(">>>>>>>>>>>>>>>>>>>>> Message: {} isSend: {} <<<<<<<<<<<<<<<<<<<<<<", response.description(), response.isOk());
                if (queuedMessage != null) {
                    sendMessage(queuedMessage);
                }
                if (forwardMessage != null) {
                    forwardMessage(forwardMessage);
                }
            }

            @Override
            public void onFailure(SendMessage request, IOException e) {
                log.info(">>>>>>>>>>>>>>>>>>>>> Message errors: {} <<<<<<<<<<<<<<<<<<<<<<", e.getMessage());
            }
        });
    }

    public void forwardMessage(ForwardMessage forwardMessage) {
        clientBot.execute(forwardMessage, new Callback<ForwardMessage, SendResponse>() {
            @Override
            public void onResponse(ForwardMessage request, SendResponse response) {
//                log.info(">>>>>>>>>>>>>>>>>>>>> Forward: {} isSend: {} <<<<<<<<<<<<<<<<<<<<<<", response.description(), response.isOk());
            }

            @Override
            public void onFailure(ForwardMessage request, IOException e) {
                log.info(">>>>>>>>>>>>>>>>>>>>> Forward errors: {} <<<<<<<<<<<<<<<<<<<<<<", e.getMessage());
            }
        });
    }

    public void editMessage(EditMessageText editMessage) {
        clientBot.execute(editMessage, new Callback<EditMessageText, BaseResponse>() {
            @Override
            public void onResponse(EditMessageText request, BaseResponse response) {
//                log.info(">>>>>>>>>>>>>>>>>>>>> Edit: {} isSend: {} <<<<<<<<<<<<<<<<<<<<<<", response.description(), response.isOk());
            }

            @Override
            public void onFailure(EditMessageText request, IOException e) {
                log.info(">>>>>>>>>>>>>>>>>>>>> Edit errors: {} <<<<<<<<<<<<<<<<<<<<<<", e.getMessage());
            }
        });
    }

    public void sendMessageWithKeyboard(Long chatId, String message) {
        sendMessageWithKeyboard(chatId, message, null);
    }

    public void sendMessageWithKeyboard(Long chatId, String message, Keyboard keyboard) {
        sendMessageWithKeyboard(chatId, message, keyboard, null);
    }

    public void sendMessageWithKeyboard(Long chatId, String message, Keyboard keyboard, Long replyMessageId) {
        sendMessageWithKeyboard(chatId, message, keyboard, replyMessageId, null);
    }

    public void sendMessageWithKeyboard(Long chatId, String message, Keyboard keyboard, Long replyMessageId, String queuedMessage) {
        SendMessage sendMessage = new SendMessage(chatId, message).parseMode(ParseMode.HTML).linkPreviewOptions(new LinkPreviewOptions().isDisabled(true));
        if (keyboard != null) {
            if (keyboard instanceof ReplyKeyboardMarkup) {
                keyboard = ((ReplyKeyboardMarkup) keyboard).oneTimeKeyboard(true).resizeKeyboard(true).selective(true);
            }
            sendMessage = sendMessage.replyMarkup(keyboard);
        }
        if (replyMessageId != null) {
            sendMessage.replyToMessageId(replyMessageId.intValue());
        }
        SendMessage sendMessage1 = queuedMessage != null ? new SendMessage(chatId, queuedMessage).parseMode(ParseMode.HTML).linkPreviewOptions(new LinkPreviewOptions().isDisabled(true)) : null;
        sendMessage(sendMessage, sendMessage1);
    }

    public void removeInlineKeyboard(Long chatId, int messageId, String text) {
        EditMessageText editMessageText = new EditMessageText(chatId, messageId, text).parseMode(ParseMode.HTML).linkPreviewOptions(new LinkPreviewOptions().isDisabled(true));
        editMessage(editMessageText);
    }

    public void sendMessageToAdminChannel(final String message) {
        logBot.execute(new SendMessage(applicationProperties.getLogChannelId(), message)
                .parseMode(ParseMode.HTML).linkPreviewOptions(new LinkPreviewOptions().isDisabled(true)), new Callback<SendMessage, SendResponse>() {
            @Override
            public void onResponse(SendMessage request, SendResponse response) {
            }

            @Override
            public void onFailure(SendMessage request, IOException e) {
                log.info(">>>>>>>>>>>>>>>>>>>>> Message errors: {} <<<<<<<<<<<<<<<<<<<<<<", e.getMessage());
            }
        });
    }

}
