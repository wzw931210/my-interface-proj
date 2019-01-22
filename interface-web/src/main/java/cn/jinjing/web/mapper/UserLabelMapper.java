package cn.jinjing.web.mapper;

import cn.jinjing.web.model.UserLabel;

public interface UserLabelMapper {
    int deleteByPrimaryKey(Long autoId);

    int insert(UserLabel record);

    int insertSelective(UserLabel record);

    UserLabel selectByPrimaryKey(Long autoId);

    int updateByPrimaryKeySelective(UserLabel record);

    int updateByPrimaryKey(UserLabel record);
}