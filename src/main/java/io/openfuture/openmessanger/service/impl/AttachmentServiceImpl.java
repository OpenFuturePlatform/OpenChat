package io.openfuture.openmessanger.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

import io.openfuture.openmessanger.repository.AttachmentRepository;
import io.openfuture.openmessanger.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    public static final String DEFAULT_BUCKET = "open-chat-main-bucket";

    private final AmazonS3 amazonS3;
    private final AttachmentRepository attachmentRepository;

    @Override
    public void upload(final MultipartFile file) throws IOException {
        ObjectMetadata data = new ObjectMetadata();
        data.setContentType(file.getContentType());
        data.setContentLength(file.getSize());
        amazonS3.putObject(DEFAULT_BUCKET, file.getOriginalFilename(), file.getInputStream(), data);

        attachmentRepository.save(file.getOriginalFilename());
    }

    @Override
    public byte[] download(final String fileName) throws IOException {
        S3Object o = amazonS3.getObject(DEFAULT_BUCKET, fileName);
        S3ObjectInputStream s3is = o.getObjectContent();

        return IOUtils.toByteArray(s3is);
    }

}
