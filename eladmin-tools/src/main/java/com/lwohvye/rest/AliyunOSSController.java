package com.lwohvye.rest;

import com.lwohvye.annotation.rest.AnonymousGetMapping;
import com.lwohvye.annotation.rest.AnonymousPostMapping;
import com.lwohvye.service.IAliyunOSSService;
import com.lwohvye.utils.result.ResultInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Hongyan Wang
 * @date 2021年09月05日 18:10
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/aliyunOSS")
@Api(tags = "工具：对象存储OSS")
public class AliyunOSSController {

    private final IAliyunOSSService aliyunOSSService;

    @AnonymousPostMapping
    @ApiOperation("分片上传")
    public ResponseEntity<Object> multipartUpload(MultipartFile file) {
        aliyunOSSService.multipartUploadFile(file);
        return new ResponseEntity<>(ResultInfo.success(), HttpStatus.OK);
    }

    @AnonymousGetMapping
    @ApiOperation("断点续传下载")
    public ResponseEntity<Object> downloadFile(String ossUri, String downloadPath) {
        aliyunOSSService.downloadFile(ossUri, downloadPath);
        return new ResponseEntity<>(ResultInfo.success(), HttpStatus.OK);
    }

}
