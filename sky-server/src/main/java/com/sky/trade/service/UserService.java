package com.sky.trade.service;

import com.sky.trade.dto.UserLoginDTO;
import com.sky.trade.entity.User;

public interface UserService {
    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    User wxLogin(UserLoginDTO userLoginDTO);
}
