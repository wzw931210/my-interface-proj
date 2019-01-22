package cn.jinjing.web.mapper;

import cn.jinjing.web.model.LabelSource;

public interface LabelSourceMapper {
    int deleteByPrimaryKey(Long autoId);

    int insert(LabelSource record);

    int insertSelective(LabelSource record);

    LabelSource selectByPrimaryKey(Long autoId);

    int updateByPrimaryKeySelective(LabelSource record);

    int updateByPrimaryKey(LabelSource record);
}