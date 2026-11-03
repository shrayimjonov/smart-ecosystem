package uz.uzkassa.cts.repository.custom;

import org.springframework.stereotype.Repository;
import uz.uzkassa.cts.dto.base.ResultList;
import uz.uzkassa.cts.dto.humoconnect.response.HumoConnectExcelResponseDto;
import uz.uzkassa.cts.dto.humoconnect.response.HumoConnectResponseDto;
import uz.uzkassa.cts.filter.HumoConnectFilter;

import java.util.List;

@Repository
public interface HumoConnectRepositoryCustom {

    ResultList<HumoConnectResponseDto> getResultList(HumoConnectFilter filter);

    List<HumoConnectExcelResponseDto> getExcelList(HumoConnectFilter filter);
}
