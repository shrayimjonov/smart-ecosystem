package uz.uzkassa.smartpos.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TelegramUpdateListener implements CommandLineRunner {
    private final TelegramBot telegramBot;
    private final TelegramService telegramService;

    @Override
    public void run(String... args) {
        telegramBot.setUpdatesListener(list -> {
            list.forEach(telegramService::exchange);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}
