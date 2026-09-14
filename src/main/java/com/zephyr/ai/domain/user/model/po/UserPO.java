package com.zephyr.ai.domain.user.model.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zephyr.ai.types.common.BasePO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 系统用户持久化对象（Persistent Object）。
 * <p>
 * 与数据库表 {@code sys_user} 一一映射，继承 {@link BasePO} 复用主键、
 * 创建人/创建时间、更新人/更新时间以及逻辑删除等审计字段。
 * 业务字段依赖 MyBatis-Plus 默认的驼峰-下划线映射，无需额外 {@code @TableField}。
 * <p>
 * 注：未使用 Lombok {@code @Builder}，因父类 {@link BasePO} 已生成 {@code builder()}，
 * 子类再次声明会导致静态方法隐藏时返回类型不兼容；如需建造者模式可考虑在
 * 父类改用 {@code @SuperBuilder}（本次不修改基础类）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_user")
public class UserPO extends BasePO {

    /**
     * 登录账号，全局唯一。
     */
    private String username;

    /**
     * BCrypt 加密后的密码哈希。
     */
    private String passwordHash;

    /**
     * 用户昵称，用于界面展示。
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
     * 微信 OpenID，用于第三方登录绑定，可为空。
     */
    private String wxOpenid;

    /**
     * 用户状态：1 正常，0 禁用。
     */
    private Integer status;

}
