package cn.jinjing.plat.user.dao;

import cn.jinjing.plat.user.pojo.BaseUserLabel;

public interface BaseUserLabelMapper {
    int deleteByPrimaryKey(Long autoId);

    int insert(BaseUserLabel record);

    int insertSelective(BaseUserLabel record);

    BaseUserLabel selectByPrimaryKey(Long autoId);

    int updateByPrimaryKeySelective(BaseUserLabel record);

    int updateByPrimaryKey(BaseUserLabel record);
}