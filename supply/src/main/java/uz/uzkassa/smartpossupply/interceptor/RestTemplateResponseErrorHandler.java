package uz.uzkassa.smartpossupply.interceptor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import uz.uzkassa.smartpossupply.web.rest.errors.ErrorDTO;
import uz.uzkassa.smartpossupply.web.rest.errors.IntegrationException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

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
        String responseAsString = toString(httpResponse.getBody());
        ErrorDTO errorDTO;
        try {
            errorDTO = new ObjectMapper().readValue(responseAsString, ErrorDTO.class);
            throw new IntegrationException(responseAsString, responseAsString, httpResponse.getStatusCode(), errorDTO);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new IntegrationException(responseAsString, responseAsString, httpResponse.getStatusCode());
        }
    }

    String toString(InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }
}
