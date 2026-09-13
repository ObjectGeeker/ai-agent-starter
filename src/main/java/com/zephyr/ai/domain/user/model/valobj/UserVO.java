package com.zephyr.ai.domain.user.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户安全展示对象。
 * <p>
 * 不包含密码哈希和微信 OpenID。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {

    /**
     * 用户 ID。
     */
    private String id;

    /**
     * 登录账号。
     */
    private String username;

    /**
     * 用户昵称。
     */
    private String nickname;

    /**
     * 用户头像地址。
     */
    private String avatar;

    /**
     * 用户角色编码列表。
     */
    private List<String> roles;

    /**
     * 用户状态：1 正常，0 禁用。
     */
    private Integer status;

    /**
     * 创建时间。
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间。
     */
    private LocalDateTime updatedAt;

}
