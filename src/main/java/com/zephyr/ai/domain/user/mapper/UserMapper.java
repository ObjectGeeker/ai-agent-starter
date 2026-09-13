package com.zephyr.ai.domain.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zephyr.ai.domain.user.model.entity.UserPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据访问接口。
 */
@Mapper
public interface UserMapper extends BaseMapper<UserPO> {
}
