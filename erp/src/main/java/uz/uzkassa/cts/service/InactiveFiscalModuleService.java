package uz.uzkassa.cts.service;

import uz.uzkassa.cts.domain.InactiveFiscalModule;
import uz.uzkassa.cts.dto.payload.OfdSyncInactiveFiscalModulePayload;

public interface InactiveFiscalModuleService extends BaseService<InactiveFiscalModule, Long> {

    void sync();

    void syncInactiveFiscalModules(OfdSyncInactiveFiscalModulePayload ofdSyncInactiveFiscalModulePayload);
}
