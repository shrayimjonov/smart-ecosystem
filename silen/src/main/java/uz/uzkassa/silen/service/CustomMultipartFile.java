package uz.uzkassa.silen.service;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Getter
@Setter
public class CustomMultipartFile implements MultipartFile {

    byte[] bytes;

    String originalName;

    String contentType;

    CustomMultipartFile(){}

    public CustomMultipartFile(byte[] bytes, String originalName, String contentType) {
        this.bytes = bytes;
        this.originalName = originalName;
        this.contentType = contentType;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getOriginalFilename() {
        return originalName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return bytes == null || bytes.length == 0;
    }

    @Override
    public long getSize() {
        return bytes.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return bytes;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public void transferTo(File file) throws IOException, IllegalStateException {
        try(FileOutputStream fos = new FileOutputStream(file)){
            fos.write(bytes);
        }
    }
}
