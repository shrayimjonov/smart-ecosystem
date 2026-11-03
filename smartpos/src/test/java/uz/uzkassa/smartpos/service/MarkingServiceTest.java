package uz.uzkassa.smartpos.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.http.ResponseEntity;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import uz.uzkassa.smartpos.dto.turon.CisInfoWrapper;
import uz.uzkassa.smartpos.dto.turon.PublicCisInfo;
import uz.uzkassa.smartpos.enums.MarkingLevel;
import uz.uzkassa.smartpos.enums.ProductGroup;
import uz.uzkassa.smartpos.integration.trueApi.TuronApi;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
class MarkingServiceTest {
    @Mock
    TuronApi turonApi;
    @InjectMocks
    MarkingService markingService;

    @Test
    void getAllChild() {
        var markings = new ArrayList<String>();
        int totalCount = 0;
        final int LEVEL3_SIZE = 13;
        final int LEVEL2_SIZE = 12;
        final int LEVEL1_SIZE = 15;
        int LEVEL3_COUNT = 43;
        int LEVEL2_COUNT = 50;
        int LEVEL1_COUNT = 78;
        int UNIT = 1000;
        totalCount += LEVEL3_COUNT * LEVEL3_SIZE * LEVEL2_SIZE * LEVEL1_SIZE;
        for (int i = 0; i < LEVEL3_COUNT; i++) {
            var marking = "3_" + UUID.randomUUID();
            markings.add(marking);
        }
        totalCount += LEVEL2_COUNT * LEVEL2_SIZE * LEVEL1_SIZE;
        for (int i = 0; i < LEVEL2_COUNT; i++) {
            var marking = "2_" + UUID.randomUUID();
            markings.add(marking);
        }
        totalCount += LEVEL1_COUNT * LEVEL1_SIZE;
        for (int i = 0; i < LEVEL1_COUNT; i++) {
            var marking = "1_" + UUID.randomUUID();
            markings.add(marking);
        }
        totalCount += UNIT;
        for (int i = 0; i < UNIT; i++) {
            var marking = UUID.randomUUID().toString();
            markings.add(marking);
        }
        doAnswer((Answer<ResponseEntity<List<CisInfoWrapper<PublicCisInfo>>>>) invocation -> {
            var resultList = new ArrayList<CisInfoWrapper<PublicCisInfo>>();
            Collection<String> argument = invocation.getArgument(1, Collection.class);
            for (String marking : argument) {
                if (marking.startsWith("3_")) {
                    var wrapper = new CisInfoWrapper<PublicCisInfo>();
                    var cisInfo = new PublicCisInfo();
                    cisInfo.setCis(marking);
                    cisInfo.setRequestedCis(marking);
                    cisInfo.setPackageType(MarkingLevel.LEVEL3.name());
                    cisInfo.setChild(IntStream.range(0, LEVEL3_SIZE)
                            .mapToObj(i -> "2_" + UUID.randomUUID()).collect(Collectors.toCollection(HashSet::new)));
                    wrapper.setCisInfo(cisInfo);
                    resultList.add(wrapper);
                } else if (marking.startsWith("2_")) {
                    var wrapper = new CisInfoWrapper<PublicCisInfo>();
                    var cisInfo = new PublicCisInfo();
                    cisInfo.setCis(marking);
                    cisInfo.setRequestedCis(marking);
                    cisInfo.setPackageType(MarkingLevel.LEVEL2.name());
                    cisInfo.setChild(IntStream.range(0, LEVEL2_SIZE)
                            .mapToObj(i -> "1_" + UUID.randomUUID()).collect(Collectors.toCollection(HashSet::new)));
                    wrapper.setCisInfo(cisInfo);
                    resultList.add(wrapper);
                } else if (marking.startsWith("1_")) {
                    var wrapper = new CisInfoWrapper<PublicCisInfo>();
                    var cisInfo = new PublicCisInfo();
                    cisInfo.setCis(marking);
                    cisInfo.setRequestedCis(marking);
                    cisInfo.setPackageType(MarkingLevel.LEVEL1.name());
                    cisInfo.setChild(IntStream.range(0, LEVEL1_SIZE)
                            .mapToObj(i -> UUID.randomUUID().toString()).collect(Collectors.toCollection(HashSet::new)));
                    wrapper.setCisInfo(cisInfo);
                    resultList.add(wrapper);
                } else {
                    var wrapper = new CisInfoWrapper<PublicCisInfo>();
                    var cisInfo = new PublicCisInfo();
                    cisInfo.setCis(marking);
                    cisInfo.setRequestedCis(marking);
                    cisInfo.setPackageType(MarkingLevel.UNIT.name());
                    wrapper.setCisInfo(cisInfo);
                    resultList.add(wrapper);
                }
            }
            return ResponseEntity.ok().body(resultList);
        }).when(turonApi).cisesPublicInfo(anyString(), anyCollection(), anyInt());

        Set<String> allChild = markingService.getAllChild(RandomStringUtils.randomAlphabetic(10), ProductGroup.Alcohol, markings);
        assertEquals(totalCount, allChild.size());
    }


    @Test
    void getAllCodes() {
        var markings = new ArrayList<String>();
        int totalCount = 0;
        final int LEVEL3_SIZE = 13;
        final int LEVEL2_SIZE = 12;
        final int LEVEL1_SIZE = 15;
        int LEVEL3_COUNT = 43;
        int LEVEL2_COUNT = 50;
        int LEVEL1_COUNT = 78;
        int UNIT = 1000;
        totalCount += LEVEL3_COUNT;
        totalCount += LEVEL3_COUNT * LEVEL3_SIZE;
        totalCount += LEVEL3_COUNT * LEVEL3_SIZE * LEVEL2_SIZE;
        totalCount += LEVEL3_COUNT * LEVEL3_SIZE * LEVEL2_SIZE * LEVEL1_SIZE;
        for (int i = 0; i < LEVEL3_COUNT; i++) {
            var marking = "3_" + UUID.randomUUID();
            markings.add(marking);
        }
        totalCount += LEVEL2_COUNT;
        totalCount += LEVEL2_COUNT * LEVEL2_SIZE;
        totalCount += LEVEL2_COUNT * LEVEL2_SIZE * LEVEL1_SIZE;
        for (int i = 0; i < LEVEL2_COUNT; i++) {
            var marking = "2_" + UUID.randomUUID();
            markings.add(marking);
        }
        totalCount += LEVEL1_COUNT;
        totalCount += LEVEL1_COUNT * LEVEL1_SIZE;
        for (int i = 0; i < LEVEL1_COUNT; i++) {
            var marking = "1_" + UUID.randomUUID();
            markings.add(marking);
        }
        totalCount += UNIT;
        for (int i = 0; i < UNIT; i++) {
            var marking = UUID.randomUUID().toString();
            markings.add(marking);
        }
        doAnswer((Answer<ResponseEntity<List<CisInfoWrapper<PublicCisInfo>>>>) invocation -> {
            var resultList = new ArrayList<CisInfoWrapper<PublicCisInfo>>();
            Collection<String> argument = invocation.getArgument(1, Collection.class);
            for (String marking : argument) {
                if (marking.startsWith("3_")) {
                    var wrapper = new CisInfoWrapper<PublicCisInfo>();
                    var cisInfo = new PublicCisInfo();
                    cisInfo.setCis(marking);
                    cisInfo.setRequestedCis(marking);
                    cisInfo.setPackageType(MarkingLevel.LEVEL3.name());
                    cisInfo.setChild(IntStream.range(0, LEVEL3_SIZE)
                            .mapToObj(i -> "2_" + UUID.randomUUID()).collect(Collectors.toCollection(HashSet::new)));
                    wrapper.setCisInfo(cisInfo);
                    resultList.add(wrapper);
                } else if (marking.startsWith("2_")) {
                    var wrapper = new CisInfoWrapper<PublicCisInfo>();
                    var cisInfo = new PublicCisInfo();
                    cisInfo.setCis(marking);
                    cisInfo.setRequestedCis(marking);
                    cisInfo.setPackageType(MarkingLevel.LEVEL2.name());
                    cisInfo.setChild(IntStream.range(0, LEVEL2_SIZE)
                            .mapToObj(i -> "1_" + UUID.randomUUID()).collect(Collectors.toCollection(HashSet::new)));
                    wrapper.setCisInfo(cisInfo);
                    resultList.add(wrapper);
                } else if (marking.startsWith("1_")) {
                    var wrapper = new CisInfoWrapper<PublicCisInfo>();
                    var cisInfo = new PublicCisInfo();
                    cisInfo.setCis(marking);
                    cisInfo.setRequestedCis(marking);
                    cisInfo.setPackageType(MarkingLevel.LEVEL1.name());
                    cisInfo.setChild(IntStream.range(0, LEVEL1_SIZE)
                            .mapToObj(i -> UUID.randomUUID().toString()).collect(Collectors.toCollection(HashSet::new)));
                    wrapper.setCisInfo(cisInfo);
                    resultList.add(wrapper);
                } else {
                    var wrapper = new CisInfoWrapper<PublicCisInfo>();
                    var cisInfo = new PublicCisInfo();
                    cisInfo.setCis(marking);
                    cisInfo.setRequestedCis(marking);
                    cisInfo.setPackageType(MarkingLevel.UNIT.name());
                    wrapper.setCisInfo(cisInfo);
                    resultList.add(wrapper);
                }
            }
            return ResponseEntity.ok().body(resultList);
        }).when(turonApi).cisesPublicInfo(anyString(), anyCollection(), anyInt());

        Set<MarkingService.MarkingWithChild> allChild = markingService.getAllCodes(RandomStringUtils.randomAlphabetic(10), ProductGroup.Alcohol, markings);
        assertEquals(totalCount, allChild.size());
    }
}