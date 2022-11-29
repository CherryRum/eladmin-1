/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.lwohvye.tools.rest;

import com.lwohvye.core.annotation.log.OprLog;
import com.lwohvye.core.exception.BadRequestException;
import com.lwohvye.tools.domain.LocalStorage;
import com.lwohvye.tools.service.ILocalStorageService;
import com.lwohvye.tools.service.dto.LocalStorageQueryCriteria;
import com.lwohvye.core.utils.FileUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author Zheng Jie
 * @date 2019-09-05
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "LocalStorageController", description = "工具：本地存储管理")
@RequestMapping("/api/localStorage")
public class LocalStorageController {

    private final ILocalStorageService localStorageService;

    @Operation(summary = "查询文件")
    @GetMapping
    public ResponseEntity<Map<String, Object>> query(LocalStorageQueryCriteria criteria, Pageable pageable) {
        return new ResponseEntity<>(localStorageService.queryAll(criteria, pageable), HttpStatus.OK);
    }

    @Operation(summary = "导出数据")
    @GetMapping(value = "/download")
    public void download(HttpServletResponse response, LocalStorageQueryCriteria criteria) throws IOException {
        localStorageService.download(localStorageService.queryAll(criteria), response);
    }

    @Operation(summary = "上传文件")
    @PostMapping
    public ResponseEntity<String> create(@RequestParam String name, @RequestParam("file") MultipartFile file) {
        localStorageService.create(name, file);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/pictures")
    @Operation(summary = "上传图片")
    public ResponseEntity<LocalStorage> upload(@RequestParam MultipartFile file) {
        // 判断文件是否为图片
        String suffix = FileUtils.getExtensionName(file.getOriginalFilename());
        if (!FileUtils.IMAGE.equals(FileUtils.getFileType(suffix))) {
            throw new BadRequestException("只能上传图片");
        }
        LocalStorage localStorage = localStorageService.create(null, file);
        return new ResponseEntity<>(localStorage, HttpStatus.OK);
    }

    @OprLog("修改文件")
    @Operation(summary = "修改文件")
    @PutMapping
    public ResponseEntity<String> update(@Validated @RequestBody LocalStorage resources) {
        localStorageService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @OprLog("删除文件")
    @DeleteMapping
    @Operation(summary = "多选删除")
    public ResponseEntity<String> delete(@RequestBody Long[] ids) {
        localStorageService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
