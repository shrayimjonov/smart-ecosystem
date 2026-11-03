package uz.uzkassa.cts.service;

import org.springframework.core.io.Resource;

public interface FileStorageService {

    Resource loadFileAsResource(String tin, String fileName);
}
