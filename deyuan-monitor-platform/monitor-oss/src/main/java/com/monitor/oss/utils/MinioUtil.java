package com.monitor.oss.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.monitor.common.common.ErrorCode;
import com.monitor.common.exception.BusinessException;
import com.monitor.oss.model.FileInfo;
import io.minio.*;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MinioUtil {

    @Resource
    MinioClient minioClient;

    @Value("${minio.endpoint}")
    String endpoint;

    @Value("${minio.bucket-name}")
    String bucketName;

    /**
     * 返回域名+桶名地址
     */
    public String getBucketUrl(){
        return endpoint+"/"+bucketName;
    }

    /**
     * 创建桶
     * @param bucket
     * @throws Exception
     */
    public void createBucket(String bucket) throws Exception {
        //查看桶是否存在
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists){
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    /**
     * 上传文件
     * @param multipartFile
     * @param bucket
     * @throws Exception
     */
    public String uploadFile(MultipartFile multipartFile, String bucket) throws Exception{
        //上传文件
        String originalFilename = multipartFile.getOriginalFilename();   // 获取原文件名
        String ext = "." + FileUtil.getSuffix(originalFilename);    // 获取文件后缀
        String prefix = FileUtil.getPrefix(originalFilename);
        String uuid = System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", "");

        // 完整路径名
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).replace("-", "");
        String filePathName = date + "-" + uuid + "-" + originalFilename;
        InputStream inputStream = multipartFile.getInputStream();
        ObjectWriteResponse objectWriteResponse = minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(filePathName)
                .stream(inputStream, -1, Integer.MAX_VALUE).build());
        // 拼接完整路径
        String ossFilePath = getBucketUrl() + "/" +filePathName;
        return ossFilePath;
    }

    /**
     * 获取所有的桶
     * @return
     * @throws Exception
     */
    public List<String> getAllBucket() throws Exception{
        List<Bucket> buckets = minioClient.listBuckets();
        return buckets.stream().map(Bucket::name).collect(Collectors.toList());
    }

    /**
     * 获取所有的问题件
     * @param bucket
     * @return
     * @throws Exception
     */
    public List<FileInfo> getAllFiles(String bucket) throws Exception {
        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucket).build());
        List<FileInfo> fileInfoList = new ArrayList<>();
        for (Result<Item> result: results) {
            FileInfo fileInfo = new FileInfo();
            Item item = result.get();
            //塞入属性
            fileInfo.setFileName(item.objectName());
            fileInfo.setDirectoryFlag(item.isDir());
            fileInfo.setEtag(item.etag());
            fileInfoList.add(fileInfo);
        }
        return fileInfoList;
    }

    /**
     * 下载文件
     * @param bucket
     * @param objectName
     * @return
     * @throws Exception
     */
    public InputStream downLoad(String bucket,String objectName) throws Exception{
        return minioClient.getObject(GetObjectArgs.builder().bucket(bucket).object(objectName).build());
    }

    /**
     * 删除桶
     * @param bucket
     * @throws Exception
     */
    public void deleteBucket(String bucket) throws Exception{
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucket).build());
    }

    /**
     * 删除文件
     * @param objectName
     * @throws Exception
     */
    public void deleteObject(String bucketName, String objectName) throws Exception {
        if (StrUtil.isBlank(bucketName) || StrUtil.isBlank(objectName)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "Bucket名称和对象名称不能为空");
        }

        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build());

    }
}