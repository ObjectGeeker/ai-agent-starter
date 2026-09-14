package com.zephyr.ai.infrastructure.auth;

import cn.dev33.satoken.stp.StpInterface;
import com.zephyr.ai.domain.user.service.IUserService;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Sa-Token 自定义权限/角色数据源实现（{@link StpInterface} SPI 扩展）。
 * <p>
 * Sa-Token 在执行 {@code @SaCheckRole}、{@code @SaCheckPermission}、{@code StpUtil.hasRole}
 * 等鉴权操作时，会回调本类获取当前账号的角色码与权限码集合。角色数据通过
 * {@link IUserService#listRoles(String)} 从 user 领域查询，最终落库到 {@code sys_user.roles}。
 * <p>
 * 作为基础设施层适配器，本类依赖领域接口 {@link IUserService} 而非具体实现，
 * 与项目中 {@code AgentRegistry -> SpringAgentRegistry} 的分层方式保持一致。
 */
@Component
public class StpInterfaceImpl implements StpInterface {

    private final IUserService userService;

    public StpInterfaceImpl(IUserService userService) {
        this.userService = userService;
    }

    /**
     * 返回指定账号拥有的权限码集合。
     * <p>
     * 当前数据模型仅定义了角色（{@code sys_user.roles}），未定义细粒度权限码，
     * 故返回空列表；后续如引入权限表可在此扩展查询逻辑。
     *
     * @param loginId   账号 ID
     * @param loginType 账号体系类型（本项目为单一体系，未做区分）
     * @return 权限码列表，不会为 {@code null}
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return Collections.emptyList();
    }

    /**
     * 返回指定账号拥有的角色标识集合。
     * <p>
     * 通过 user 领域服务按 {@code loginId} 查库获取角色编码列表。
     *
     * @param loginId   账号 ID
     * @param loginType 账号体系类型（本项目为单一体系，未做区分）
     * @return 角色编码列表，不会为 {@code null}
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        if (loginId == null) {
            return Collections.emptyList();
        }
        return userService.listRoles(loginId.toString());
    }
}
