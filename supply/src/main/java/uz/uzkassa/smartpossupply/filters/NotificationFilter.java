package uz.uzkassa.smartpossupply.filters;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Powered by: Shuxratjon Rayimjonov
 * Date: 06.02.2023 17:40
 */
@Getter
@Setter
public class NotificationFilter extends BaseFilter {

    String applicationType;

    @JsonProperty("read")
    Boolean read;
}
