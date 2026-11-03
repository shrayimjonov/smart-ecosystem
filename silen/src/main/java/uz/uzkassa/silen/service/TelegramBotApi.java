package uz.uzkassa.silen.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import uz.uzkassa.silen.config.httpclient.CustomRequest;
import uz.uzkassa.silen.integration.BaseIntegrationClient;
import uz.uzkassa.silen.transaction.AfterCommitExecutorImpl;

import java.util.Arrays;
import java.util.Map;

@Component
@Slf4j
public class TelegramBotApi extends BaseIntegrationClient {
    private final Environment environment;
    private final String botToken;
    @Getter
    private final String logChannelId;
    private final AfterCommitExecutorImpl afterCommitExecutor;

    public TelegramBotApi(CustomRequest customRequest, ObjectMapper objectMapper,
                          @Value("${application.logBotUrl}") String startEndpoint,
                          @Value("${application.logChannelId}") String logChannelId,
                          @Value("${application.logBotToken}") String botToken,
                          Environment environment, AfterCommitExecutorImpl afterCommitExecutor) {
        super(customRequest, objectMapper, startEndpoint);
        this.environment = environment;
        this.botToken = botToken;
        this.logChannelId = logChannelId;
        this.afterCommitExecutor = afterCommitExecutor;
    }

    @Override
    protected Map<String, String> evaluateHeaders(String token) {
        return Map.of();
    }

    @Override
    protected String getBaseUrl() {
        return startEndpoint + botToken + "/sendMessage";
    }

    public void sendMessageToAdminChannel(final String message) {
        if (StringUtils.isEmpty(message)) {
            log.error("Message is empty");
            return;
        }
        try {
            String messageToSend = "PROFILE: " + Arrays.toString(environment.getActiveProfiles()) + "\n" + message;
            SendMessage sendMessage = new SendMessage(getLogChannelId(), messageToSend, "html");
            customRequest.sendPostRequest(getBaseUrl(), null, convertBodyToJsonString(sendMessage));
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        log.debug("Message sent to admin channel: {}", message);
    }

    record SendMessage(String chat_id, String text, String parse_mode) {
    }
}
