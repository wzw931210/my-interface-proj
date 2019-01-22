package cn.jinjing.plat.user.dao;

import cn.jinjing.plat.api.entity.CacheUserLabel;
import cn.jinjing.plat.user.pojo.UserInfo;

import java.util.List;

public interface UserInfoMapper {

    UserInfo logon(UserInfo user);

    int updateRemainByUser(UserInfo user);

    List<CacheUserLabel> queryUserLabels(String userCode);

    List<UserInfo> queryAllUser();
}