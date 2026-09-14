package com.sky.skyapi.client;

import com.sky.entity.AddressBook;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Map;

@FeignClient(name = "sky-user")
public interface UserClient {
    @GetMapping("/inner/user/count")
    Integer count(@RequestParam Map<String, Object> map) ;

    @GetMapping("/inner/addressBook/{id}")
    AddressBook getById(@PathVariable Long id);
}
