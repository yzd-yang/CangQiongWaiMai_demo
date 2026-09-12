package com.sky.trade.skyapi.client;

import com.sky.trade.entity.AddressBook;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "sky-user")
public interface AddressBookClient {
    @GetMapping("/inner/addressBook/{id}")
    AddressBook getById(@PathVariable Long id);
}