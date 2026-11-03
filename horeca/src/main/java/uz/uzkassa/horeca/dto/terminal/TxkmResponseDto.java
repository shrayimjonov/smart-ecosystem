package uz.uzkassa.horeca.dto.terminal;

import java.util.List;

public record TxkmResponseDto(boolean success, Integer code, String message, List<TxkmResponseData> data) {
}
