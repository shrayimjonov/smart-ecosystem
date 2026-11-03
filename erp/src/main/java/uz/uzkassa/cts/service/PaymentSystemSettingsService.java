package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.PaymentSystemSettings;

public interface PaymentSystemSettingsService extends BaseService<PaymentSystemSettings, Long>{
    PaymentSystemSettings getFirstRow();
}
