package com.zephyr.ai.domain.auth.model.entity;

import com.zephyr.ai.domain.auth.model.valobj.LoginTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录领域命令。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginCommandEntity {

    private LoginTypeEnum loginType;

    private String username;

    private String password;
}
