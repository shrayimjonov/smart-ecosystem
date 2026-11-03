package uz.uzkassa.smartpossupply.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.uzkassa.smartpossupply.repository.PartnerRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static lombok.AccessLevel.PRIVATE;


@Service
@Transactional(readOnly = true)
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PartnerService extends BaseService {

    PartnerRepository partnerRepository;

    public List<Long> getCompanyList() {
        return Stream.concat(
                partnerRepository.findAllSuppliers().stream(),
                partnerRepository.findAllCustomers().stream()
        ).distinct().collect(Collectors.toList());
    }
}
