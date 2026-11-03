package uz.uzkassa.smartpossupply.filters;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TelegramOrderFilter extends BaseFilter {

    String token;

    Long telegramUserId;
}
