package com.sky.skyuser.controller.inner;

import com.sky.skyuser.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/inner/user")
public class UserInnerController {

    @Autowired
    private UserMapper userMapper;
    @GetMapping("/count")
    public Integer count(@RequestParam Map<String, Object> map) {
        return userMapper.countByMap(map);
    }

}
