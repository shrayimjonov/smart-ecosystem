package uz.uzkassa.smartpossupply.report.execution;

import org.springframework.stereotype.Component;
import uz.uzkassa.smartpossupply.config.ApplicationProperties;
import uz.uzkassa.smartpossupply.report.engine.ReportErrorCode;
import uz.uzkassa.smartpossupply.report.engine.ReportException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

@Component
public class ReportPreviewConcurrencyGuard {

    private final int maxConcurrentPerCompany;
    private final ConcurrentHashMap<Long, Semaphore> companyPermits = new ConcurrentHashMap<>();

    public ReportPreviewConcurrencyGuard(ApplicationProperties applicationProperties) {
        this.maxConcurrentPerCompany = applicationProperties.getReportEngine().getMaxConcurrentPerCompany();
    }

    public Lease acquire(Long companyId) {
        if (companyId == null) {
            throw new IllegalArgumentException("companyId must not be null");
        }
        Semaphore semaphore = companyPermits.computeIfAbsent(
                companyId,
                ignored -> new Semaphore(maxConcurrentPerCompany)
        );
        if (!semaphore.tryAcquire()) {
            throw ReportException.limit(
                    ReportErrorCode.MAX_CONCURRENT_PREVIEWS_EXCEEDED,
                    "Превышен лимит одновременно выполняемых отчётов для компании"
            );
        }
        return new Lease(semaphore);
    }

    public static final class Lease implements AutoCloseable {

        private final Semaphore semaphore;
        private boolean released;

        private Lease(Semaphore semaphore) {
            this.semaphore = semaphore;
        }

        @Override
        public void close() {
            if (!released) {
                released = true;
                semaphore.release();
            }
        }
    }
}
