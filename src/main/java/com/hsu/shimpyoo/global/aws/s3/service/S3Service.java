package com.hsu.shimpyoo.global.aws.s3.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class S3Service {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucket; //버킷 이름

    public String uploadFile(MultipartFile multipartFile, String userId, String date, int number) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();

        String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));
        String newFilename = userId + "_" + date + "_" + number + extension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        amazonS3.putObject(bucket, newFilename, multipartFile.getInputStream(), metadata);
        return amazonS3.getUrl(bucket, newFilename).toString();
    }

    public void deleteFile(String filename)  {
        amazonS3.deleteObject(bucket, filename);
    }


}
