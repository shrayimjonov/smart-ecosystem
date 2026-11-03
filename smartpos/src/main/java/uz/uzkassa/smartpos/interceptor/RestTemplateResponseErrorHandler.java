package uz.uzkassa.smartpos.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import uz.uzkassa.smartpos.dto.ErrorDTO;
import uz.uzkassa.smartpos.exceptions.IntegrationException;

import java.io.IOException;

import static org.springframework.http.HttpStatus.Series.CLIENT_ERROR;
import static org.springframework.http.HttpStatus.Series.SERVER_ERROR;

@Slf4j
@Component

public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
        return (
                ((HttpStatus) httpResponse.getStatusCode()).series() == CLIENT_ERROR
                || ((HttpStatus) httpResponse.getStatusCode()).series() == SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse httpResponse) throws IOException {
        String responseAsString = new String(httpResponse.getBody().readAllBytes());
        ErrorDTO errorDTO = null;
        try {
            errorDTO = new ObjectMapper().readValue(responseAsString, ErrorDTO.class);
        } catch (JsonProcessingException e) {
            log.error("Error while parsing error response: {}", responseAsString, e);
        }
        throw new IntegrationException(responseAsString, errorDTO);
    }

}
