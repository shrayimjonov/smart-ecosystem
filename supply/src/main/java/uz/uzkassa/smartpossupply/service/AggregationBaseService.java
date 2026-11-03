package uz.uzkassa.smartpossupply.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import uz.uzkassa.smartpossupply.domain.company.CompanyAggregationInfo;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfo;
import uz.uzkassa.smartpossupply.dto.aslbelgi.CisInfoResponse;
import uz.uzkassa.smartpossupply.integration.AslBelgiClient;

import java.util.Collections;
import java.util.Set;

@Component
@Slf4j
public abstract class AggregationBaseService extends BaseService {

    @Autowired
    protected AslBelgiClient aslBelgiClient;

    protected CisInfo cisesInfoWrapper(String cutSimpleCode) {
        if (StringUtils.isEmpty(cutSimpleCode)) {
            return null;
        }

        CompanyAggregationInfo companyAggregationInfo = validateAslBelgiAndGetAggregationInfo();
        CisInfoResponse[] cisesInfoResponse = aslBelgiClient.cisesInfo(getCurrentCompanyTin(), companyAggregationInfo.getAslBelgiToken(), Collections.singleton(cutSimpleCode));
        if (cisesInfoResponse[0] != null) {
            CisInfoResponse cisInfo = cisesInfoResponse[0];
            return cisInfo.getCisInfo();
        }
        return null;
    }

    protected CisInfoResponse[] cisesInfoWrapper(Set<String> cutSimpleCode) {
        if (CollectionUtils.isEmpty(cutSimpleCode)) {
            return null;
        }
        CompanyAggregationInfo companyAggregationInfo = validateAslBelgiAndGetAggregationInfo();
        return aslBelgiClient.cisesInfo(getCurrentCompanyTin(), companyAggregationInfo.getAslBelgiToken(), cutSimpleCode);
    }
}
