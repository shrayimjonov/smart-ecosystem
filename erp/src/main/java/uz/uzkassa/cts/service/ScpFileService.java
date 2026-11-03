package uz.uzkassa.cts.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public interface ScpFileService {

    String upload(String fileName, byte[] fileByte);

    ByteArrayOutputStream download(String remoteDestination);

    void delete(String remoteDestination);

    InputStream getInputStream(String path);
}
