package cn.jinjing.web.mapper;

import cn.jinjing.web.model.UserLabelSource;

public interface UserLabelSourceMapper {
    int deleteByPrimaryKey(Long autoId);

    int insert(UserLabelSource record);

    int insertSelective(UserLabelSource record);

    UserLabelSource selectByPrimaryKey(Long autoId);

    int updateByPrimaryKeySelective(UserLabelSource record);

    int updateByPrimaryKey(UserLabelSource record);
}