package cn.jinjing.plat.user.dao;

import cn.jinjing.plat.user.pojo.BaseUserLabelSource;

public interface BaseUserLabelSourceMapper {
    int deleteByPrimaryKey(Long autoId);

    int insert(BaseUserLabelSource record);

    int insertSelective(BaseUserLabelSource record);

    BaseUserLabelSource selectByPrimaryKey(Long autoId);

    int updateByPrimaryKeySelective(BaseUserLabelSource record);

    int updateByPrimaryKey(BaseUserLabelSource record);
}