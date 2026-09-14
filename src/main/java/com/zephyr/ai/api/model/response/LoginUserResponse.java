package com.zephyr.ai.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录成功响应中的脱敏用户信息。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserResponse {

    private String id;

    private String username;

    private String nickname;

    private String avatar;
}
