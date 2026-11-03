package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.constants.Constants;
import uz.uzkassa.smartpossupply.dto.aslbelgi.aic.ProductTnVedResponse;
import uz.uzkassa.smartpossupply.dto.aslbelgi.aic.ProductTnVedResponseList;
import uz.uzkassa.smartpossupply.integration.AslBelgiClient;
import uz.uzkassa.smartpossupply.utils.MarkUtils;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class AsilBelgisiService extends BaseService implements Constants {
    AslBelgiClient aslBelgiClient;


    public ProductTnVedResponse getTnVedCode(String barcode) {
        ProductTnVedResponseList tnVedCodeDetailsResponseList = aslBelgiClient.getTnVedCodeFromTrueApi(getCompanyAggregationInfo().getAslBelgiToken(), MarkUtils.GTIN_FORMAT.format(Long.parseLong(barcode)));
        if (CollectionUtils.isEmpty(tnVedCodeDetailsResponseList.getResults()))
            throw validationService.badRequestExceptionThrow("По данный баркуду тнвэд код не найден", "Tnved code not found").get();
        return Optional.ofNullable(tnVedCodeDetailsResponseList.getResults().getFirst()).orElseThrow(() -> validationService.badRequestExceptionThrow("По данный баркуду тнвэд код не найден", "Tnved code not found").get());
    }

}
