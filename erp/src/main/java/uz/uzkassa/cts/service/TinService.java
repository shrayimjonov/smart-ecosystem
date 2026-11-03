package uz.uzkassa.cts.service;

import uz.uzkassa.cts.dto.tin.TinDTO;
import uz.uzkassa.cts.dto.tin.integration.OfdCustomerBalanceResponseDTO;
import uz.uzkassa.cts.dto.tin.integration.OfdResponseDTO;
import uz.uzkassa.cts.dto.tin.integration.OfdTokenResponseDTO;
import uz.uzkassa.cts.dto.tin.integration.SignedFormDto;

public interface TinService {

    TinDTO getCompanyByTin(String tin);

    TinDTO getCustomerByTin(String tin);

    OfdCustomerBalanceResponseDTO getCustomerBalance(String login, String tin);

    OfdTokenResponseDTO getToken(String tin);

    OfdResponseDTO verify(SignedFormDto signedFormDto);
}
