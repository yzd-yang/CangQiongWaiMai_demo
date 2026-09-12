package com.sky.skyuser.controller.inner;

import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.skyuser.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inner/addressBook")
public class AddressBookInnerController {

    @Autowired
    private AddressBookService addressBookService;

    @GetMapping("/{id}")
    public Result<AddressBook> getById(@PathVariable Long id) {
        return Result.success(addressBookService.getById(id));
    }
}