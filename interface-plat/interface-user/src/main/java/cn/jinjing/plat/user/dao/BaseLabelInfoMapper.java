package cn.jinjing.plat.user.dao;

import cn.jinjing.plat.user.pojo.BaseLabelInfo;

public interface BaseLabelInfoMapper {
    int deleteByPrimaryKey(Long autoId);

    int insert(BaseLabelInfo record);

    int insertSelective(BaseLabelInfo record);

    BaseLabelInfo selectByPrimaryKey(Long autoId);

    int updateByPrimaryKeySelective(BaseLabelInfo record);

    int updateByPrimaryKey(BaseLabelInfo record);
}