package uz.uzkassa.smartpos.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.company.CompanyTuronInfo;
import uz.uzkassa.smartpos.integration.trueApi.TuronApi;
import uz.uzkassa.smartpos.repository.postgres.company.CompanyTuronInfoRepository;
import uz.uzkassa.smartpos.security.SecurityUtils;
import uz.uzkassa.smartpos.security.TokenUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TuronService {
    private final CompanyTuronInfoRepository turonInfoRepository;
    private final TuronApi turonApi;

    @Transactional
    public void update(String token) {
        CompanyTuronInfo turonInfo = turonInfoRepository.findById(SecurityUtils.getCurrentCompanyId())
                .orElseGet(() -> new CompanyTuronInfo(SecurityUtils.getCurrentCompanyId(), token));
        turonInfo.setToken(token);
        turonInfoRepository.save(turonInfo);
    }


    @Transactional
    public void update(Long id, String token) {
        CompanyTuronInfo turonInfo = turonInfoRepository.findById(id).orElseGet(() -> new CompanyTuronInfo(id, token));
        turonInfo.setToken(token);
        turonInfoRepository.save(turonInfo);
    }

    public void refresh() {
        CompanyTuronInfo company = turonInfoRepository.getReferenceById(SecurityUtils.getCurrentCompanyId());
        String token = turonApi.refresh(company.getToken());
        company.setToken(token);
        company.setReceivedDate(LocalDateTime.now());
        company.setExpirationDate(TokenUtils.getExpirationTime(token));
        turonInfoRepository.save(company);
    }


    @Scheduled(cron = "30 30 * * * *")
    public void hourlyRefresh() {
        log.info("Refreshing infos true tokens");
        List<CompanyTuronInfo> infos = turonInfoRepository.findAllExpiredTokens();
        var correct = new ArrayList<CompanyTuronInfo>();
        var incorrect = new ArrayList<CompanyTuronInfo>();
        infos.forEach(info -> {
            try {
                String token = turonApi.refresh(info.getToken());
                info.setToken(token);
                correct.add(info);
            } catch (Exception e) {
                log.error(e.getMessage());
                incorrect.add(info);
            }
        });
        turonInfoRepository.saveAll(correct);
        turonInfoRepository.deleteAll(incorrect);
    }

}
