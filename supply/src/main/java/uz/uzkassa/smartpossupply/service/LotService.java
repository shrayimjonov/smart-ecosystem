package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.dto.lot.LotDTO;
import uz.uzkassa.smartpossupply.dto.lot.LotPrefixDTO;
import uz.uzkassa.smartpossupply.integration.FacturaRestClient;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class LotService extends BaseService {

    FacturaRestClient facturaRestClient;

    public LotDTO get(String lotId, String buyerTin) {
        return facturaRestClient.getLot(lotId, buyerTin);
    }

    public LotPrefixDTO[] getPrefixes(String lotId, String buyerTin) {
        return facturaRestClient.getLotPrefix(lotId, buyerTin);
    }
}
