package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.FileUploadUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
public class CommonController {

    private final FileUploadUtil fileUploadUtil;

    @Autowired
    public CommonController(FileUploadUtil fileUploadUtil) {
        this.fileUploadUtil = fileUploadUtil;
    }

    @ApiOperation("文件上传")
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
