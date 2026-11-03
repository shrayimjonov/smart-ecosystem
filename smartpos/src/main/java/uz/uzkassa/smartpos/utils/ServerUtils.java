package uz.uzkassa.smartpos.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import uz.uzkassa.smartpos.exceptions.BadRequestException;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public final class ServerUtils {
    private static final DecimalFormat decimalFormat = new DecimalFormat("#,###");
    private static final AtomicLong sequenceNumber = new AtomicLong(1);

    public static String generateVirtualSerialNumber(int seqNumber) {
        String serialNumber = String.valueOf(seqNumber);
        if (seqNumber < 10) {
            serialNumber = "000000" + seqNumber;
        } else if (seqNumber < 100) {
            serialNumber = "00000" + seqNumber;
        } else if (seqNumber < 1000) {
            serialNumber = "0000" + seqNumber;
        } else if (seqNumber < 10000) {
            serialNumber = "000" + seqNumber;
        } else if (seqNumber < 100000) {
            serialNumber = "00" + seqNumber;
        } else if (seqNumber < 1000000) {
            serialNumber = "0" + seqNumber;
        }
        return StringUtils.join("№ - ", serialNumber);
    }

    public static String formatPrice(BigDecimal price) {
        return decimalFormat.format(price);
    }

    public static boolean isAggregation(String code) {
        return code.matches("^[(]?0?0?[)]?\\d.{17}$");
    }

    public static String fileToBase64(String fileUrl) {
        try {
            return encodeFileToBase64(IOUtils.toByteArray(URI.create(fileUrl)));
        } catch (IOException e) {
            log.error("File parse error:{}", e.getMessage());
            throw new BadRequestException("Could not read file " + fileUrl + " Error: " + e.getMessage());
        }
    }

    public static String encodeFileToBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }


    public static ResponseEntity<Resource> getResponseEntity(ByteArrayResource resource, String name) {
        if (resource == null) {
            return ResponseEntity.status(204).build();
        }
        String encodedFilename = URLEncoder.encode(name, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFilename + ".xlsx")
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(resource);
    }

    public static String generateUniqueBarcode() {
        final String format = String.format("%03d", sequenceNumber.getAndIncrement() % 1000);
        return StringUtils.join(System.currentTimeMillis(), format);
    }



    public static <T> List<Set<T>> partition(Set<T> set, int limit) {
        var result = new ArrayList<Set<T>>();
        var current = new HashSet<T>();
        for (T s : set) {
            current.add(s);
            if (current.size() >= limit) {
                result.add(current);
                current = new HashSet<>();
            }
        }
        if (!current.isEmpty()) {
            result.add(current);
        }
        return result;
    }
}
