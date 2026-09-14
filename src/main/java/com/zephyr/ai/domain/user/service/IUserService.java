package com.zephyr.ai.domain.user.service;

import java.util.List;

/**
 * 用户领域服务接口。
 * <p>
 * 定义 user 领域对外暴露的能力契约，由 {@code UserServiceImpl} 提供具体实现。
 */
public interface IUserService {

    /**
     * 查询指定账号拥有的角色标识集合，供 Sa-Token 角色鉴权使用。
     *
     * @param userId 用户 ID（对应 Sa-Token 的 loginId）
     * @return 角色编码列表；用户不存在、无角色或数据非法时返回空列表，不会返回 {@code null}
     */
    List<String> listRoles(String userId);
}
