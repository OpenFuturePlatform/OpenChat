package io.openfuture.openmessanger.service.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import io.openfuture.openmessanger.service.AttachmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final AmazonS3 amazonS3;

    @Override
    public void upload(final String bucketName, final String fileName, final InputStream stream) {
        try {
            amazonS3.putObject(bucketName, fileName, stream, new ObjectMetadata());
        } catch (AmazonServiceException e) {
            log.error(e.getErrorMessage());
        }
    }

    @Override
    public FileOutputStream download(final String bucketName, final String fileName) {
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
        try {
            S3Object o = s3.getObject(bucketName, fileName);
            S3ObjectInputStream s3is = o.getObjectContent();
            FileOutputStream fos = new FileOutputStream(fileName);
            byte[] read_buf = new byte[1024];
            int read_len;
            while ((read_len = s3is.read(read_buf)) > 0) {
                fos.write(read_buf, 0, read_len);
            }
            s3is.close();
            fos.close();

            return fos;
        } catch (AmazonServiceException e) {
            log.error(e.getErrorMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return null;
    }

}
