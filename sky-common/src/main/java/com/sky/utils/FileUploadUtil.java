package com.sky.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件上传工具类
 */
@Component
@Slf4j
public class FileUploadUtil {
    @Value("${sky.file.upload-path:./sky-server/src/main/resources/static}")
    private String uploadPath;

    @Value("${sky.file.access-prefix:http://localhost:8080/}")
    private String accessPrefix;

    /**
     * 文件上传
     *
     * @param file 上传的文件
     * @return 文件访问地址
     */
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        // 获取原始文件名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new RuntimeException("文件名不能为空");
        }

        // 获取文件扩展名
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf(".");
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }

        // 生成唯一文件名
        String fileName = UUID.randomUUID().toString().replace("-", "") + extension;

        // 创建上传目录
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            if (!uploadDir.mkdirs()) {
                throw new RuntimeException("创建上传目录失败: " + uploadPath);
            }
        }

        // 构建完整路径
        Path filePath = Paths.get(uploadPath, fileName);

        try {
            // 保存文件
            Files.write(filePath, file.getBytes());
            log.info("文件上传成功: {}", fileName);
        } catch (IOException e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }

        // 返回访问地址
        String accessUrl = accessPrefix + fileName;
        log.info("文件访问地址: {}", accessUrl);
        
        return accessUrl;
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件访问地址
     * @return 是否删除成功
     */
    public boolean delete(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }

        try {
            // 从URL中提取文件名
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
            Path filePath = Paths.get(uploadPath, fileName);

            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.info("文件删除成功: {}", fileName);
            } else {
                log.warn("文件不存在: {}", fileName);
            }

            return deleted;
        } catch (IOException e) {
            log.error("文件删除失败", e);
            return false;
        }
    }
}
