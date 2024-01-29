package com.d209.childmade._common.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class S3Util {

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;  // S3 Bucket name

    @Autowired
    private AmazonS3 amazonS3;

    /**
     * 컷 동영상을 방 id(roodId)와 대사 순서(scriptNum)를 통해
     * S3상의 'bucketName/cutvideo/roodId/' 경로에 scriptNum으로 저장하는 메서드
     */
    public String uploadCutVideo(MultipartFile file, Long roomId, int scriptNum){
        createFolder(bucketName + "/cutvideo", Long.toString(roomId));
        ObjectMetadata objectMetadata = getObjectMetadata(file);
        try {
            amazonS3.putObject(new PutObjectRequest(bucketName + "/cutvideo/" + Long.toString(roomId), Integer.toString(scriptNum), file.getInputStream(), objectMetadata));
        } catch (IOException e) {
//            log.error("Error uploading file to S3", e);
        }
        return amazonS3.getUrl(bucketName + "/cutvideo/" + Long.toString(roomId), Integer.toString(scriptNum)).toString();
    }

    /**
     * roomId에 해당하는 방에서 녹화된 모든 컷 동영상들을
     * S3에서 다운로드 받아 byte[]형태로 리스트에 저장한다.
     */
    public List<byte[]> downloadCutVideos(Long roodId, int scriptCount){
        List<byte[]> cutVideos = new ArrayList<>();
        for(int i = 1; i <= scriptCount; i++){
            S3Object s3Object = amazonS3.getObject(bucketName + "/cutvideo/" + Long.toString(roodId), Integer.toString(scriptCount) + ".mp4");
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            try {
                cutVideos.add(IOUtils.toByteArray(inputStream));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return cutVideos;
    }

    /**
     * S3상에 새로운 폴더를 생성하는 메서드
     * S3상의 dirName 경로에 folderName으로 폴더를 생성한다.
     */
    public void createFolder(String dirName, String folderName){
        amazonS3.putObject(dirName, folderName + "/", new ByteArrayInputStream(new byte[0]), new ObjectMetadata());
    }

    /**
     * file의 파일 타입과 크기를 저장한 ObjectMetadata을 반환하는 메서드
     */
    public ObjectMetadata getObjectMetadata(MultipartFile file){
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        return objectMetadata;
    }
}
