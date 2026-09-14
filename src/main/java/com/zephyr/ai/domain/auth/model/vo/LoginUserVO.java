package com.zephyr.ai.domain.auth.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录成功后的用户信息。
 * <p>
 * 仅包含可以返回给客户端的脱敏字段，不包含密码哈希、Token、角色、OpenID 或审计字段。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserVO {

    private String id;

    private String username;

    private String nickname;

    private String avatar;
}
