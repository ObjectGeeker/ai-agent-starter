package com.zephyr.ai.domain.user.model.vo;

import com.zephyr.ai.types.common.BaseVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 系统用户视图对象（View Object）。
 * <p>
 * 用于领域层与外部（Controller / RPC / 消息）之间的数据传输，继承 {@link BaseVO}
 * 复用主键与审计字段。骨架阶段字段与 {@code UserPO} 业务字段一一对应，
 * 实际对外传输时建议按场景剔除敏感字段（例如 {@link #passwordHash}）。
 * <p>
 * 注：未使用 Lombok {@code @Builder}，原因同 {@code UserPO}（父类已生成 {@code builder()}）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserVO extends BaseVO {

    /**
     * 登录账号。
     */
    private String username;

    /**
     * BCrypt 密码哈希；敏感字段，对外输出时应剔除。
     */
    private String passwordHash;

    /**
     * 用户昵称。
     */
    private String nickname;

    /**
     * 用户头像地址（URL）。
     */
    private String avatar;

    /**
     * 角色编码 JSON 字符串数组，例如 {@code ["USER"]}。
     */
    private String roles;

    /**
     * 微信 OpenID。
     */
    private String wxOpenid;

    /**
     * 用户状态：1 正常，0 禁用。
     */
    private Integer status;

}
