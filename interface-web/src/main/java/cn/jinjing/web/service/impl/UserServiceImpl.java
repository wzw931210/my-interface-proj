package cn.jinjing.web.service.impl;

import cn.jinjing.web.mapper.UserInfoMapper;
import cn.jinjing.web.model.UserInfo;
import cn.jinjing.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public UserInfo login(UserInfo user){
        return userInfoMapper.login(user);
    }
}
