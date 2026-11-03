package uz.uzkassa.cts.service.impl;

import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.uzkassa.cts.config.ApplicationProperties;
import uz.uzkassa.cts.constants.FileConstants;
import uz.uzkassa.cts.service.ScpFileService;
import uz.uzkassa.cts.service.ValidationService;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Properties;

import static uz.uzkassa.cts.constants.MessageConstants.FILE_UPLOAD_FAILED_UNEXPECTED_ERROR;

@Service
@Slf4j
public class ScpFileServiceImpl implements ScpFileService {

    private final ApplicationProperties applicationProperties;
    private final ValidationService validationService;

    public ScpFileServiceImpl(ApplicationProperties applicationProperties, ValidationService validationService) {
        this.applicationProperties = applicationProperties;
        this.validationService = validationService;
    }

    private Session getSession() throws JSchException {
        JSch jsch = new JSch();
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        Session jschSession = jsch.getSession(applicationProperties.getSshStorageProperty().getUsername(), applicationProperties.getSshStorageProperty().getRemoteHost());
        jschSession.setPassword(applicationProperties.getSshStorageProperty().getPassword());
        jschSession.setConfig(config);
        return jschSession;
    }

    @Override
    public String upload(String fileName, byte[] fileByte) {
        Session jschSession = null;
        ChannelSftp channelSftp = null;
        boolean hasError = false;
        try {
            jschSession = getSession();
            // 10 seconds session timeout
            jschSession.connect();

            channelSftp = (ChannelSftp) jschSession.openChannel("sftp");
            // 5 seconds timeout
            channelSftp.connect();

            // transfer file from local to remote server
            channelSftp.put(new ByteArrayInputStream(fileByte), applicationProperties.getSshStorageProperty().getRemoteDestination() + "/" + fileName);

        } catch (JSchException | SftpException e) {
            log.error(e.getMessage());
            hasError = true;
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
                log.info("channelSftp disconnected");
            }
            if (jschSession != null) {
                jschSession.disconnect();
                log.info("jschSession disconnected");
            }
        }
        if (hasError) {
            throw validationService.badRequestExceptionThrow(FILE_UPLOAD_FAILED_UNEXPECTED_ERROR).get();
        }
        String downloadUrl = applicationProperties.getSshStorageProperty().getDownloadUrl() + fileName;
        log.info("upload url: {}", downloadUrl);

        return downloadUrl;
    }

    @Override
    public ByteArrayOutputStream download(String path) {
        String filename = path;
        if (path.startsWith(FileConstants.STORAGE_BASE_URL)) {
            filename = path.split(FileConstants.STORAGE_BASE_URL)[1];
        }
        Session jschSession = null;
        ByteArrayOutputStream outputStream = null;
        try {
            jschSession = getSession();
            jschSession.connect();

            ChannelSftp channelSftp = (ChannelSftp) jschSession.openChannel("sftp");
            channelSftp.connect();

            try {
                outputStream = new ByteArrayOutputStream();
                BufferedOutputStream buff = new BufferedOutputStream(outputStream);
                channelSftp.get(applicationProperties.getSshStorageProperty().getRemoteDestination() + "/" + filename, buff);
            } catch (SftpException e) {
                log.error(e.getMessage());
            } finally {
                channelSftp.disconnect();
            }
        } catch (JSchException e) {
            log.error(e.getMessage());
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
        return outputStream;
    }

    @Override
    public void delete(String remoteDestination) {
        Session jschSession = null;
        ChannelSftp channelSftp = null;
        try {
            jschSession = getSession();
            jschSession.connect();

            channelSftp = (ChannelSftp) jschSession.openChannel("sftp");
            channelSftp.connect();

            channelSftp.rm(applicationProperties.getSshStorageProperty().getRemoteDestination() + "/" + remoteDestination);
        } catch (JSchException | SftpException e) {
            log.error(e.getMessage());
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }

    @Override
    public InputStream getInputStream(String path) {
        String filename = path;
        if (path.startsWith(FileConstants.STORAGE_BASE_URL)) {
            filename = path.split(FileConstants.STORAGE_BASE_URL)[1];
        }

        Session jschSession = null;
        ChannelSftp channelSftp = null;

        try {
            jschSession = getSession();
            jschSession.connect();

            channelSftp = (ChannelSftp) jschSession.openChannel("sftp");
            channelSftp.connect();

            String remotePath = applicationProperties.getSshStorageProperty().getRemoteDestination() + "/" + filename;

            return channelSftp.get(remotePath);
        } catch (JSchException | SftpException e) {
            log.error("Error while downloading file from SFTP: {}", e.getMessage());
            throw new RuntimeException("Failed to download file from SFTP", e);
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }
}
