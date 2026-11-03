package uz.uzkassa.smartpos.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpos.domain.postgres.catalog.Mxik;
import uz.uzkassa.smartpos.dto.nic.MxikDTO;
import uz.uzkassa.smartpos.exceptions.EntityNotFound;
import uz.uzkassa.smartpos.integration.tasnif.TasnifApi;
import uz.uzkassa.smartpos.integration.tasnif.dto.*;
import uz.uzkassa.smartpos.repository.postgres.catalog.MxikRepository;
import uz.uzkassa.smartpos.repository.postgres.catalog.ProductBranchRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class MxikService {
    private final MxikRepository mxikRepository;
    private final TasnifApi tasnifApi;
    private final ProductBranchRepository productBranchRepository;
    private LocalizationService localizationService;


    @Scheduled(cron = "0 0 1-2 * * *")
    public void syncMxiks() {
        log.info("mxik-sync: {}", LocalDateTime.now());
        long endDate = LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC).toEpochMilli();
        long startDate = LocalDateTime.now().minusDays(2).plusMinutes(1).toInstant(ZoneOffset.UTC).toEpochMilli();
        TasnifResponseDTO<List<MxikHistoryDTO>> body = tasnifApi.historyByTime(endDate,
                startDate, null, null, 100_000, null).getBody();
        if (body != null && CollectionUtils.isNotEmpty(body.getData())) {
            Map<String, Mxik> mxikMap = mxikRepository.findAllByMxikIn(body.getData().stream().map(MxikHistoryDTO::getMxik).toList())
                    .stream().collect(Collectors.toMap(Mxik::getMxik, Function.identity()));
            List<String> deletedMxiks = new ArrayList<>();
            List<Mxik> mxiks = new ArrayList<>();
            body.getData().forEach(mxikHistoryDTO -> {
                if ("-1".equals(mxikHistoryDTO.getStatus())) {
                    deletedMxiks.add(mxikHistoryDTO.getMxik());
                } else if ("3".equals(mxikHistoryDTO.getStatus())) {
                    mxiks.add(Mxik.merge(mxikMap.getOrDefault(mxikHistoryDTO.getMxik(), null), mxikHistoryDTO, false));
                } else {
                    deletedMxiks.add(mxikHistoryDTO.getMxik());
                    mxiks.add(Mxik.merge(mxikMap.getOrDefault(mxikHistoryDTO.getNewMxikCOde(), null), mxikHistoryDTO, true));
                }
            });
            if (CollectionUtils.isNotEmpty(deletedMxiks)) {
                mxikRepository.deleteAllByMxikIn(deletedMxiks);
                deletedMxiks.forEach(productBranchRepository::deleteAllByVatBarcodeAndDeletedFalse);
            }
            if (CollectionUtils.isNotEmpty(mxiks)) {
                mxikRepository.saveAll(mxiks);
            }
        }
    }

    @Scheduled(cron = "0 0 11 * * *")
    public void syncDetails() {
        int size = 500;
        int page = 0;
        Page<Mxik> mxikPage = mxikRepository.findAll(PageRequest.of(page++, size, Sort.by("mxik")));
        while (!mxikPage.isEmpty()) {
            log.info("package-sync: page: {}, total: {}", mxikPage.getNumber(), mxikPage.getTotalElements());
            Map<String, Mxik> mxikMap = mxikPage.stream().collect(Collectors.toMap(Mxik::getMxik, Function.identity()));
            try {
                ResponseEntity<TasnifResponseDTO<List<MxikCheckWrapperDTO>>> response = tasnifApi.checkMxiksFromHazelcast(mxikPage.map(Mxik::getMxik).stream().collect(Collectors.toList()), null, null);
                if (HttpStatus.OK.equals(response.getStatusCode()) && response.getBody() != null && CollectionUtils.isNotEmpty(response.getBody().getData())) {
                    List<Mxik> deletedMxiks = new ArrayList<>();
                    List<Mxik> mxiks = new ArrayList<>();
                    response.getBody().getData().forEach(mxikCheckWrapperDTO -> {
                        MxikCheckInfoDTO info = mxikCheckWrapperDTO.getInfo();
                        if (info != null && info.getName() != null && CollectionUtils.isNotEmpty(info.getPackageNames())) {
                            Mxik mxik = mxikMap.getOrDefault(mxikCheckWrapperDTO.getMxikCode(), null);
                            mxik = Mxik.merge(mxik, info);
                            mxiks.add(mxik);
                        } else {
                            deletedMxiks.add(mxikMap.get(mxikCheckWrapperDTO.getMxikCode()));
                        }
                    });
                    if (CollectionUtils.isNotEmpty(deletedMxiks)) {
                        mxikRepository.deleteAll(deletedMxiks);
                        deletedMxiks.forEach(mxik -> productBranchRepository.deleteAllByVatBarcodeAndDeletedFalse(mxik.getMxik()));
                    }
                    if (CollectionUtils.isNotEmpty(mxiks)) {
                        mxikRepository.saveAll(mxiks);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                mxikPage = mxikRepository.findAll(PageRequest.of(page++, size, Sort.by("mxik")));
            }
        }
    }

    @Transactional
    public List<MxikPackage> partialSync(String mxikCode) {
        ResponseEntity<TasnifResponseDTO<List<MxikCheckWrapperDTO>>> response = tasnifApi.checkMxiksFromHazelcast(Collections.singletonList(mxikCode), null, null);
        if (response.getBody() != null && CollectionUtils.isNotEmpty(response.getBody().getData())) {
            MxikCheckWrapperDTO wrapperDTO = response.getBody().getData().getFirst();
            if (wrapperDTO.getInfo() != null) {
                Mxik mxik = mxikRepository.findByMxik(wrapperDTO.getMxikCode()).orElse(null);
                mxik = Mxik.merge(mxik, wrapperDTO.getInfo());
                List<MxikPackage> packagesFromTasnif = getPackagesFromTasnif(wrapperDTO.getMxikCode());
                if (CollectionUtils.isNotEmpty(packagesFromTasnif)) {
                    mxik.setPackages(packagesFromTasnif);
                }
                mxikRepository.save(mxik);
                return mxik.getPackages();
            }
        }
        return null;
    }


    @Transactional
    public List<MxikDTO> add(List<String> mxiks) {
        TasnifResponseDTO<List<MxikCheckWrapperDTO>> body = tasnifApi.checkMxiksFromHazelcast(mxiks, null, null).getBody();
        if (body == null || body.getData() == null) return null;
        List<Mxik> entities = new ArrayList<>();
        body.getData().forEach(wrapper -> {
            if (wrapper.getInfo() != null && wrapper.getInfo().getName() != null) {
                entities.add(Mxik.merge(null, wrapper.getInfo()));
            }
        });
        mxikRepository.saveAll(entities);
        return entities.stream().map(MxikDTO::new).collect(Collectors.toList());
    }

    public List<MxikDTO> searchProduct(String searchKey, String mxikCode, String barcode) {
        if (StringUtils.isNotEmpty(mxikCode)) {
            if (mxikCode.length() == 17) {
                Optional<Mxik> mxikOptional = mxikRepository.findByMxik(mxikCode);
                return mxikOptional.map(mxik -> List.of(new MxikDTO(mxik)))
                        .orElseGet(() -> createAndGet(mxikCode));
            }
            return mxikRepository.findAllByMxikContains(mxikCode).stream().map(MxikDTO::new).toList();
        } else if (StringUtils.isNotEmpty(barcode)) {
            return mxikRepository.findAllByBarcode(barcode).stream().map(MxikDTO::new).toList();
        } else {
            searchKey = URLDecoder.decode(searchKey, StandardCharsets.UTF_8);
            return mxikRepository.findAllByName("%" + searchKey.toLowerCase() + "%").stream().map(MxikDTO::new).toList();
        }
    }

    private List<MxikDTO> createAndGet(String mxikCode) {
        ResponseEntity<TasnifResponseDTO<List<MxikCheckWrapperDTO>>> response = tasnifApi.checkMxiksFromHazelcast(Collections.singletonList(mxikCode), null, null);
        if (response.getBody() != null && CollectionUtils.isNotEmpty(response.getBody().getData())) {
            MxikCheckWrapperDTO wrapperDTO = response.getBody().getData().getFirst();
            if (wrapperDTO.getInfo() != null) {
                Mxik mxik = Mxik.merge(null, wrapperDTO.getInfo());
                mxikRepository.save(mxik);
                return List.of(new MxikDTO(mxik));
            }
        }
        return Collections.emptyList();
    }

    public List<MxikPackage> getPackagesByMxik(String mxikCode) {
        return mxikRepository.findByMxik(mxikCode).map(Mxik::getPackages)
                .orElseThrow(() -> new EntityNotFound(localizationService.getMessage("ikpu.must.correct")));
    }


    private List<MxikPackage> getPackagesFromTasnif(String mxikCode) {
        ResponseEntity<TasnifResponseDTO<List<MxikPackage>>> response = tasnifApi.getPackages(mxikCode);
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody().getData();
        }
        return null;
    }


    /**
     * Sync all mxiks from Tasnif with datetime
     */
    public void fullTimeSync() {
        LocalDateTime start = LocalDateTime.now().minusYears(5);
        while (start.isBefore(LocalDateTime.now().plusDays(1))) {
            log.info("mxik-sync: {}", start);
            try {
                int page = 0;
                TasnifResponseDTO<List<MxikHistoryDTO>> body = tasnifApi.historyByTime(start.plusDays(6).toInstant(ZoneOffset.UTC).toEpochMilli(),
                        start.toInstant(ZoneOffset.UTC).toEpochMilli(), null, page++, 1000, null).getBody();
                while (body != null && body.getData() != null && CollectionUtils.isNotEmpty(body.getData())) {
                    log.info("mxik-sync: {}, {}", page - 1, body.getRecordTotal());
                    Map<String, Mxik> mxikMap = mxikRepository.findAllByMxikIn(body.getData().stream().map(MxikHistoryDTO::getMxik).toList())
                            .stream().collect(Collectors.toMap(Mxik::getMxik, Function.identity()));
                    List<String> deletedMxiks = new ArrayList<>();
                    List<Mxik> mxiks = new ArrayList<>();
                    body.getData().forEach(mxikHistoryDTO -> {
                        if ("-1".equals(mxikHistoryDTO.getStatus())) {
                            deletedMxiks.add(mxikHistoryDTO.getMxik());
                        } else if ("3".equals(mxikHistoryDTO.getStatus())) {
                            mxiks.add(Mxik.merge(mxikMap.getOrDefault(mxikHistoryDTO.getMxik(), null), mxikHistoryDTO, false));
                        } else {
                            deletedMxiks.add(mxikHistoryDTO.getMxik());
                            mxiks.add(Mxik.merge(mxikMap.getOrDefault(mxikHistoryDTO.getNewMxikCOde(), null), mxikHistoryDTO, true));
                        }
                    });
                    if (CollectionUtils.isNotEmpty(deletedMxiks)) {
                        mxikRepository.deleteAllByMxikIn(deletedMxiks);
                    }
                    if (CollectionUtils.isNotEmpty(mxiks)) {
                        mxikRepository.saveAll(mxiks);
                    }
                    body = tasnifApi.historyByTime(start.plusDays(6).toInstant(ZoneOffset.UTC).toEpochMilli(),
                            start.toInstant(ZoneOffset.UTC).toEpochMilli(), null, page++, 1000, null).getBody();
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                start = start.plusDays(6);
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * 0")
    public void syncBasicMxikPackages() {
        int page = 0;
        int size = 500;
        Page<Mxik> mxikPage = mxikRepository.findAll(PageRequest.of(page++, size, Sort.by("mxik")));
        while (!mxikPage.isEmpty()) {
            log.info("package-sync: page: {}, total: {}", mxikPage.getNumber(), mxikPage.getTotalElements());
            try {
                ResponseEntity<TasnifResponseDTO<List<BasicMxikPackage>>> response = tasnifApi.getBasicPackages(mxikPage.map(Mxik::getMxik).stream().collect(Collectors.toSet()));
                if (HttpStatus.OK.equals(response.getStatusCode()) && response.getBody() != null && CollectionUtils.isNotEmpty(response.getBody().getData())) {
                    var packageMap = response.getBody().getData().stream().collect(Collectors.toMap(BasicMxikPackage::getMxikCode, Function.identity(), (o, n) -> n));
                    mxikPage.forEach(mxik -> {
                        if (CollectionUtils.isEmpty(mxik.getPackages())) return;
                        MxikPackage mxikPackage = CollectionUtils.isNotEmpty(mxik.getPackages()) ? mxik.getPackages().getFirst() : null;
                        if (packageMap.containsKey(mxik.getMxik())) {
                            BasicMxikPackage basic = packageMap.get(mxik.getMxik());
                            if (mxik.getPackages().stream().anyMatch(p -> p.getCode().equals(basic.getBasicPackageCode()))) {
                                mxikPackage = new MxikPackage(basic);
                            }
                        }
                        mxik.setDefaultPackage(mxikPackage);
                    });
                }
                mxikRepository.saveAll(mxikPage);
            } catch (Exception e) {
                log.error(e.getMessage());
            } finally {
                mxikPage = mxikRepository.findAll(PageRequest.of(page++, size, Sort.by("mxik")));
            }
        }
        log.info("package-sync: done");
    }

}
