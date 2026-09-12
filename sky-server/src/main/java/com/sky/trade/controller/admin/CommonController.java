package com.sky.trade.controller.admin;

import com.sky.trade.result.Result;
import com.sky.trade.utils.FileUploadUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/admin/common")
@Tag(name = "通用接口")
public class CommonController {

    private final FileUploadUtil fileUploadUtil;

    @Autowired
    public CommonController(FileUploadUtil fileUploadUtil) {
        this.fileUploadUtil = fileUploadUtil;
    }

    @Operation(summary = "文件上传")
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile  file){
        log.info("文件上传：{}",file);
        String url = fileUploadUtil.upload(file);

        if(!url.isEmpty()){
            log.info("文件上传成功：{}",url);
            return Result.success(url);
        }
        return Result.error("上传失败");
    }
}
