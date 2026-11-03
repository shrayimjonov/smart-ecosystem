package uz.uzkassa.smartpossupply.social.telegram.handler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import uz.uzkassa.smartpossupply.enums.TelegramCommand;
import uz.uzkassa.smartpossupply.repository.TelegramIntegrationRepository;
import uz.uzkassa.smartpossupply.social.telegram.common.TelegramWebAppCommand;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TelegramMessageHandler implements TelegramHandler<Message> {

    TelegramWebAppCommand telegramWebAppCommand;
    TelegramIntegrationRepository telegramIntegrationRepository;

    @Override
    public void handle(Message message, TelegramBot telegramBot) {
        String text = message.text();
        if (text != null && text.startsWith(TelegramCommand.START.getCommand())) {
            Long chatId = message.chat().id();
            User user = message.from();
            telegramIntegrationRepository.findFirstByBotToken(telegramBot.getToken()).ifPresent(telegramIntegration -> {
                telegramWebAppCommand.startWebAppCommand(chatId, user.id(), telegramBot, telegramIntegration.getBotType());
            });
        }
    }
}
