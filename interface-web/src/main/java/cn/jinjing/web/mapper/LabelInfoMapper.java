package cn.jinjing.web.mapper;

import cn.jinjing.web.model.LabelInfo;

public interface LabelInfoMapper {
    int deleteByPrimaryKey(Long autoId);

    int insert(LabelInfo record);

    int insertSelective(LabelInfo record);

    LabelInfo selectByPrimaryKey(Long autoId);

    int updateByPrimaryKeySelective(LabelInfo record);

    int updateByPrimaryKey(LabelInfo record);
}