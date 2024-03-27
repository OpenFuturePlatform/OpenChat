package io.openfuture.openmessanger.service;

import java.io.FileOutputStream;
import java.io.InputStream;

public interface AttachmentService {

    void upload(String bucketName, String fileName, InputStream stream);

    FileOutputStream download(String bucketName, String fileName);

}
