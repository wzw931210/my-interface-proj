package cn.jinjing.plat.user.dao;

import cn.jinjing.plat.user.pojo.BaseUserInfo;

public interface BaseUserInfoMapper {
    int deleteByPrimaryKey(Integer autoId);

    int insert(BaseUserInfo record);

    int insertSelective(BaseUserInfo record);

    BaseUserInfo selectByPrimaryKey(Integer autoId);

    int updateByPrimaryKeySelective(BaseUserInfo record);

    int updateByPrimaryKey(BaseUserInfo record);
}