package com.zephyr.ai.domain.user.service.impl;

import cn.hutool.json.JSONUtil;
import com.zephyr.ai.domain.user.model.po.UserPO;
import com.zephyr.ai.domain.user.service.IUserService;
import com.zephyr.ai.infrastructure.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

/**
 * 用户领域服务默认实现。
 * <p>
 * 通过构造器注入基础设施层的 {@link UserMapper} 完成数据访问。当前提供角色查询能力，
 * 供 Sa-Token 的 {@code StpInterface} 扩展回调使用。
 */
@Slf4j
@Service
public class UserServiceImpl implements IUserService {

    /**
     * 用户表 Mapper，由 MyBatis-Plus 提供基础 CRUD 能力。
     */
    private final UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    /**
     * 查询指定账号的角色编码集合。
     * <p>
     * 按主键查询 {@code sys_user}（MyBatis-Plus 自动附加逻辑删除条件），读取 {@code roles}
     * 字段（JSON 字符串数组，例如 {@code ["USER","ADMIN"]}）并解析为列表。
     * 用户不存在、字段为空或非法 JSON 时返回空列表。
     *
     * @param userId 用户 ID（对应 Sa-Token 的 loginId）
     * @return 角色编码列表，不会为 {@code null}
     */
    @Override
    public List<String> listRoles(String userId) {
        if (!StringUtils.hasText(userId)) {
            return Collections.emptyList();
        }
        UserPO user = userMapper.selectById(userId);
        if (user == null || !StringUtils.hasText(user.getRoles())) {
            return Collections.emptyList();
        }
        String roles = user.getRoles();
        if (!JSONUtil.isTypeJSONArray(roles)) {
            log.warn("用户 [{}] 的 roles 字段不是合法 JSON 数组，已忽略：{}", userId, roles);
            return Collections.emptyList();
        }
        return JSONUtil.parseArray(roles).toList(String.class);
    }

}
