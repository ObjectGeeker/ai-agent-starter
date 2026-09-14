package com.zephyr.ai.infrastructure.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zephyr.ai.domain.user.model.po.UserPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户表 {@code sys_user} 的 MyBatis-Plus Mapper。
 * <p>
 * 通过继承 {@link BaseMapper} 获得基础 CRUD 能力（插入、按主键查询/更新/删除、
 * 条件构造器查询等），骨架阶段不声明任何自定义方法，后续按业务需要扩展。
 */
@Mapper
public interface UserMapper extends BaseMapper<UserPO> {
}
