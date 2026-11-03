package uz.uzkassa.smartpos.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
@RequiredArgsConstructor
public class IdGeneratorService {
    private final JdbcClient jdbcClient;
    private final Queue<Long> idPool = new LinkedList<>();
    private static final int POOL_SIZE = 100; // Size of the ID pool
    private final Lock lock = new ReentrantLock();

    public Long generateId() {
        lock.lock();
        try {
            if (idPool.isEmpty()) {
                refillPool();
            }
            return idPool.poll();
        } finally {
            lock.unlock();
        }
    }

    private void refillPool() {
        Long maxId = jdbcClient.sql("select nextval('global_id_seq')").query(Long.class).single();
        for (long i = 0; i < POOL_SIZE; i++) {
            idPool.add(maxId++);
        }
    }
}
