package io.openfuture.openmessanger.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    void upload(MultipartFile file) throws IOException;

    byte[] download(String fileName) throws IOException;
}
