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

    // 파일 업로드
    public String uploadFile(MultipartFile multipartFile, Long userId, String date, int number) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();

        // 확장자 추출
        String extension = originalFilename.substring(originalFilename.lastIndexOf('.'));

        // 파일 이름 변경
        String newFilename = userId + "_" + date + "_" + number + extension;

        //파일의 메타데이터 생성
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        // s3에 파일 업로드
        amazonS3.putObject(bucket, newFilename, multipartFile.getInputStream(), metadata);

        // 업로드한 파이르이 url 반환
        return amazonS3.getUrl(bucket, newFilename).toString();
    }

    // 파일 삭제
    public void deleteFile(String filename)  {
        amazonS3.deleteObject(bucket, filename);
    }


}
