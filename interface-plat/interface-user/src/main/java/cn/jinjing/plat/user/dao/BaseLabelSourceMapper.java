package cn.jinjing.plat.user.dao;

import cn.jinjing.plat.user.pojo.BaseLabelSource;

public interface BaseLabelSourceMapper {
    int deleteByPrimaryKey(Long autoId);

    int insert(BaseLabelSource record);

    int insertSelective(BaseLabelSource record);

    BaseLabelSource selectByPrimaryKey(Long autoId);

    int updateByPrimaryKeySelective(BaseLabelSource record);

    int updateByPrimaryKey(BaseLabelSource record);
}