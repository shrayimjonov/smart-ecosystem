package uz.uzkassa.smartpossupply.service;

import uz.uzkassa.smartpossupply.constants.MessageConstants;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uz.uzkassa.smartpossupply.domain.address.District;
import uz.uzkassa.smartpossupply.dto.address.nominatim.NominatimAddressRootResponse;
import uz.uzkassa.smartpossupply.dto.address.nominatim.NominatimPoints;
import uz.uzkassa.smartpossupply.dto.address.yandex.YandexAddressRootResponse;
import uz.uzkassa.smartpossupply.dto.address.yandex.YandexLocationNameRequest;
import uz.uzkassa.smartpossupply.dto.address.yandex.YandexLocationNameResponse;
import uz.uzkassa.smartpossupply.enums.LanguageEnum;
import uz.uzkassa.smartpossupply.repository.DistrictRepository;
import uz.uzkassa.smartpossupply.utils.SecurityUtils;
import uz.uzkassa.smartpossupply.utils.WordUtils;
import uz.uzkassa.smartpossupply.web.rest.errors.AppGlobalException;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
//@Transactional(readOnly = true)
@Slf4j
public class YandexService extends BaseService {

    RestTemplate restTemplate;
    DistrictRepository districtRepository;

    public NominatimPoints getPointsById(Long districtId) {
        return districtRepository.findById(districtId).map(district -> {
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                    .fromHttpUrl("https://nominatim.openstreetmap.org/")
                    .queryParam("polygon_geojson", 1)
                    .queryParam("q", String.join("%20", district.getNameUz().split(" ")))
                    .queryParam("format", "jsonv2")
                    .queryParam("limit", 1)
                    .queryParam("accept-language", SecurityUtils.getCurrentRequestLanguageEnum().name());

            try {
                NominatimAddressRootResponse[] addressResponse = restTemplate.getForObject(
                        uriComponentsBuilder.encode().build(true).toString().replace("%20", "%"),
                        NominatimAddressRootResponse[].class
                );
                if (addressResponse != null && addressResponse.length > 0) {
                    return new NominatimPoints(addressResponse[0].getLat(), addressResponse[0].getLon());
                }
                throw new AppGlobalException(localizationService.localize(MessageConstants.ERROR), localizationService.localize("Address not found"), HttpStatus.BAD_REQUEST);
            } catch (Exception e) {
                throw new AppGlobalException(localizationService.localize(MessageConstants.ERROR), localizationService.localize(MessageConstants.TIME_OUT_ERROR), HttpStatus.REQUEST_TIMEOUT);
            }
        }).orElseThrow(validationService.entityNotFoundExceptionThrow(District.class.getSimpleName(), "id", districtId));
    }

    public YandexLocationNameResponse getLocationName(YandexLocationNameRequest yandexLocationNameRequest) {

        LanguageEnum languageEnum = yandexLocationNameRequest.getLanguage() != null ? LanguageEnum.cyrillic.name().equals(yandexLocationNameRequest.getLanguage()) ?
                LanguageEnum.uz :
                LanguageEnum.valueOf(yandexLocationNameRequest.getLanguage()) : LanguageEnum.ru;

        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder
                .fromHttpUrl(applicationProperties.getYandexAddressConfig().getUrl())
                .queryParam("apikey", applicationProperties.getYandexAddressConfig().getApiKey())
                .queryParam("format", "json")
                .queryParam("geocode", yandexLocationNameRequest.getLongitude() + "," + yandexLocationNameRequest.getLatitude())
                .queryParam("lang", languageEnum.name() + "-" + languageEnum.name().toUpperCase());

        try {
            YandexAddressRootResponse yandexResponse = restTemplate.getForObject(uriComponentsBuilder.toUriString(), YandexAddressRootResponse.class);
            if (yandexResponse == null || yandexResponse.getResponse() == null) {
                return new YandexLocationNameResponse();
            }
            String name = yandexResponse.getResponse().getYandexGeoObjectCollection().getYandexFeatureMember().get(0).getYandexGeoObject().getYandexMetaDataProperty().getYandexGeocoderMetaData().getText();
            if (LanguageEnum.cyrillic.name().equals(yandexLocationNameRequest.getLanguage())) {
                name = WordUtils.latinToCyrill(name);
            }
            return new YandexLocationNameResponse(name);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return new YandexLocationNameResponse();
        }
    }
}
