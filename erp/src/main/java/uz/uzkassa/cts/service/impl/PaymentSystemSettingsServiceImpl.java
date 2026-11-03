package uz.uzkassa.cts.service.impl;

import org.springframework.stereotype.Service;
import uz.uzkassa.cts.domain.PaymentSystemSettings;
import uz.uzkassa.cts.repository.PaymentSystemSettingsRepository;
import uz.uzkassa.cts.service.PaymentSystemSettingsService;

@Service
public class PaymentSystemSettingsServiceImpl extends BaseServiceImpl<PaymentSystemSettings, Long> implements PaymentSystemSettingsService {

    private final PaymentSystemSettingsRepository repository;

    public PaymentSystemSettingsServiceImpl(PaymentSystemSettingsRepository repository) {
        super(repository);
        this.repository = repository;
    }

    public PaymentSystemSettings getFirstRow(){
        return repository.findFirstByOrderById();
    }
}
