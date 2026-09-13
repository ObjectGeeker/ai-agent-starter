package com.zephyr.ai.domain.user.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户持久化对象。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_user", autoResultMap = true)
public class UserPO {

    /**
     * 正常状态。
     */
    public static final Integer STATUS_ENABLED = 1;

    /**
     * 禁用状态。
     */
    public static final Integer STATUS_DISABLED = 0;

    /**
     * 用户 ID，由应用侧生成。
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 登录账号，微信-only 用户可以为空。
     */
    @TableField("username")
    private String username;

    /**
     * BCrypt 密码哈希，禁止保存明文密码。
     */
    @JsonIgnore
    @ToString.Exclude
    @TableField("password_hash")
    private String passwordHash;

    /**
     * 用户昵称。
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 用户头像地址。
     */
    @TableField("avatar")
    private String avatar;

    /**
     * 用户角色编码列表，以 JSON 数组形式持久化。
     */
    @Builder.Default
    @TableField(value = "roles", typeHandler = JacksonTypeHandler.class)
    private List<String> roles = new ArrayList<>(List.of("USER"));

    /**
     * 微信 OpenID，当前只支持单个微信应用。
     */
    @JsonIgnore
    @ToString.Exclude
    @TableField("wx_openid")
    private String wxOpenid;

    /**
     * 用户状态：1 正常，0 禁用。
     */
    @TableField("status")
    private Integer status;

    /**
     * 创建时间。
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间。
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

}
