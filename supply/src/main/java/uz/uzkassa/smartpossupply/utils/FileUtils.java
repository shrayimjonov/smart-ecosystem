package uz.uzkassa.smartpossupply.utils;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import uz.uzkassa.smartpossupply.enums.FileSizeType;
import uz.uzkassa.smartpossupply.service.CustomMultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static uz.uzkassa.smartpossupply.constants.FileConstants.HIGH_PHOTO_WIDTH;
import static uz.uzkassa.smartpossupply.constants.FileConstants.LOW_PHOTO_HEIGHT;
import static uz.uzkassa.smartpossupply.constants.FileConstants.MEDIUM_PHOTO_WIDTH;
@Slf4j
public final class FileUtils {

    private static MultipartFile resizePhoto(MultipartFile photo, FileSizeType fileSizeType) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(photo.getInputStream());
        } catch (IOException e) {
            log.error("Reading object error: {}", photo);
        }
        if (image != null) {
            int width;
            int height;
            float n;
            if (FileSizeType.LOW.equals(fileSizeType)) {
                n = 100 - ((float) image.getHeight() - LOW_PHOTO_HEIGHT) / image.getHeight() * 100;
                width = Math.round((image.getWidth() * n / 100));
                height = LOW_PHOTO_HEIGHT;
            } else if (FileSizeType.MEDIUM.equals(fileSizeType)) {
                n = 100 - ((float) image.getWidth() - MEDIUM_PHOTO_WIDTH) / image.getWidth() * 100;
                height = Math.round((image.getHeight() * n / 100));
                width = MEDIUM_PHOTO_WIDTH;
            } else {
                n = 100 - ((float) image.getWidth() - HIGH_PHOTO_WIDTH) / image.getWidth() * 100;
                height = Math.round((image.getHeight() * n / 100));
                width = HIGH_PHOTO_WIDTH;
            }
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.SCALE_SMOOTH);
            Graphics2D graphics2D = resizedImage.createGraphics();
            graphics2D.drawImage(image, 0, 0, width, height, null);
            graphics2D.dispose();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                ImageIO.write(resizedImage, "jpg", outputStream);
                outputStream.flush();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
            return new CustomMultipartFile(outputStream.toByteArray(), photo.getOriginalFilename(), photo.getContentType());
        }
        return null;
    }

    public static Map<FileSizeType, MultipartFile> resizePhoto(MultipartFile photo) {
        Map<FileSizeType, MultipartFile> files = new LinkedHashMap<>();
        MultipartFile lowPhoto = resizePhoto(photo, FileSizeType.LOW);
        if (lowPhoto != null) {
            files.put(FileSizeType.LOW, lowPhoto);
        }
        MultipartFile mediumPhoto = resizePhoto(photo, FileSizeType.MEDIUM);
        if (mediumPhoto != null) {
            files.put(FileSizeType.MEDIUM, mediumPhoto);
        }
        MultipartFile highPhoto = resizePhoto(photo, FileSizeType.HIGH);
        if (highPhoto != null) {
            files.put(FileSizeType.HIGH, highPhoto);
        }
        return files;
    }

}
