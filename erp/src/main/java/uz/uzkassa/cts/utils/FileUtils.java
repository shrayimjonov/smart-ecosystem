package uz.uzkassa.cts.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.cts.dto.cases.caseproperty.CustomMultipartFile;
import uz.uzkassa.cts.web.rest.errors.BadRequestException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;

@Slf4j
public final class FileUtils {


    public static String getFileNameSizeAsString(long fileSize) {
        try {
            return org.apache.commons.io.FileUtils.byteCountToDisplaySize(fileSize);
        } catch (Exception e) {
            log.error(e.getMessage());
            return "0 KB";
        }
    }

    public static String fileToBase64(String fileUrl) {
        try {
            return encodeFileToBase64(IOUtils.toByteArray(new URL(fileUrl)));
        } catch (IOException e) {
            log.error("File parse error:{}", e.getMessage());
            throw new BadRequestException("Could not read file " + fileUrl + " Error: " + e.getMessage());
        }
    }

    @Deprecated
    public static String encodeFileToBase64(String fileUrl) {
        InputStream inputStream = null;
        try {
            inputStream = new URL(fileUrl).openStream();
            byte[] bytes = IOUtils.toByteArray(inputStream);
            String result = encodeFileToBase64(bytes);
            inputStream.close();
            return result;
        } catch (Exception e) {
            log.error("File parse error: ", e);
            log.error(e.getMessage());
            throw new BadRequestException("Could not read file " + fileUrl + " Error: " + e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }

    public static String encodeFileToBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }


    public static MultipartFile getMultipartFileFromUrl(String fileUrl) throws IOException {
        URL url = new URL(fileUrl);
        byte[] targetArray;
        try (BufferedInputStream bis = new BufferedInputStream(url.openStream())) {

            try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

                int nRead;
                byte[] data = new byte[4];

                while ((nRead = bis.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();
                targetArray = buffer.toByteArray();
            }
        }

        return new CustomMultipartFile(
                targetArray,
                FilenameUtils.getName(url.getPath()),
                url.openConnection().getContentType());
    }

}
