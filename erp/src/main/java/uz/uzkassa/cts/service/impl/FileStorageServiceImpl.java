package uz.uzkassa.cts.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.constants.MessageConstants;
import uz.uzkassa.cts.service.FileStorageService;
import uz.uzkassa.cts.service.ValidationService;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    private final Path fileStorageLocation;
    private final ValidationService validationService;

    public FileStorageServiceImpl(ValidationService validationService) {
        this.validationService = validationService;
        this.fileStorageLocation = Paths.get("/downloadFile").toAbsolutePath().normalize();
    }

    @Override
    public Resource loadFileAsResource(String tin, String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(tin + File.separator + fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw validationService.entityNotFoundExceptionThrow(MessageConstants.FILE).get();
            }
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
            throw validationService.entityNotFoundExceptionThrow(MessageConstants.FILE).get();
        }
    }
}
