package uz.uzkassa.smartpossupply.social.telegram.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.domain.telegram.TelegramIntegration;
import uz.uzkassa.smartpossupply.repository.TelegramIntegrationRepository;
import uz.uzkassa.smartpossupply.social.telegram.handler.TelegramMessageHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TelegramBotListener implements CommandLineRunner, DisposableBean {

    TelegramIntegrationRepository telegramIntegrationRepository;
    TelegramMessageHandler messageHandler;

    Map<Long, TelegramBot> activeBots = new ConcurrentHashMap<>();

    public TelegramBotListener(TelegramIntegrationRepository telegramIntegrationRepository, TelegramMessageHandler messageHandler) {
        this.telegramIntegrationRepository = telegramIntegrationRepository;
        this.messageHandler = messageHandler;
    }

    @Override
    public void run(String... args) {
        log.info("Initializing Telegram bot listeners...");
        telegramIntegrationRepository.findAll().forEach(this::startBot);
        log.info("Telegram bot initialization completed. Active bots: {}", activeBots.size());
    }

    public synchronized void startBot(TelegramIntegration integration) {
        if (activeBots.containsKey(integration.getId())) {
            log.info("Bot already active for integration ID: {}", integration.getId());
            return;
        }

        try {
            TelegramBot telegramBot = new TelegramBot.Builder(integration.getBotToken())
                    .apiUrl("http://127.0.0.1:8080/bot")
                    .build();

            telegramBot.setUpdatesListener(updates -> {
                try {
                    updates.forEach(update -> {
                        try {
                            messageHandler.handle(update.message(), telegramBot);
                        } catch (Exception e) {
                            log.error("Error handling update {} for bot {}", update.updateId(), integration.getId(), e);
                        }
                    });
                    return UpdatesListener.CONFIRMED_UPDATES_ALL;
                } catch (Exception e) {
                    log.error("Error processing updates for bot {}", integration.getId(), e);
                    return UpdatesListener.CONFIRMED_UPDATES_NONE;
                }
            });

            activeBots.put(integration.getId(), telegramBot);
            log.info(" Successfully started bot for integration ID: {}", integration.getId());
        } catch (Exception e) {
            log.error(" Failed to start bot for integration ID: {}", integration.getId(), e);
        }
    }

    public synchronized void stopBot(Long integrationId) {
        TelegramBot bot = activeBots.remove(integrationId);
        if (bot != null) {
            try {
                bot.removeGetUpdatesListener();
                log.info(" Stopped bot for integration ID: {}", integrationId);
            } catch (Exception e) {
                log.error(" Error stopping bot {}", integrationId, e);
            }
        } else {
            log.warn("No active bot found for integration ID: {}", integrationId);
        }
    }

    @Override
    public void destroy() {
        log.info("Shutting down all Telegram bot listeners...");
        activeBots.forEach((id, bot) -> {
            try {
                bot.removeGetUpdatesListener();
                log.debug("Removed listener for bot ID: {}", id);
            } catch (Exception e) {
                log.error("Error removing updates listener for bot ID: {}", id, e);
            }
        });
        activeBots.clear();
        log.info(" Telegram bot listeners shutdown completed");
    }
}
