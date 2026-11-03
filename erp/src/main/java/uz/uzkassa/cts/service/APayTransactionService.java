package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.APayTransaction;
import uz.uzkassa.cts.dto.apaytransaction.request.APayTransactionRequestDto;
import uz.uzkassa.cts.dto.apaytransaction.response.APayClientInfoResponseDto;
import uz.uzkassa.cts.dto.apaytransaction.response.APayTransactionResponseDto;

public interface APayTransactionService extends BaseService<APayTransaction, Long> {

    APayTransactionResponseDto create(APayTransactionRequestDto dto);

    APayClientInfoResponseDto getClientInfoByTin(String tin);
}
