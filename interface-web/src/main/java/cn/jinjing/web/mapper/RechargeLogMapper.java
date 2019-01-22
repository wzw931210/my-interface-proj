package cn.jinjing.web.mapper;

import cn.jinjing.web.model.RechargeLog;

public interface RechargeLogMapper {
    int deleteByPrimaryKey(Integer autoId);

    int insert(RechargeLog record);

    int insertSelective(RechargeLog record);

    RechargeLog selectByPrimaryKey(Integer autoId);

    int updateByPrimaryKeySelective(RechargeLog record);

    int updateByPrimaryKey(RechargeLog record);
}