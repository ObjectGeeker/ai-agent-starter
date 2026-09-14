package com.zephyr.ai.domain.auth.service;

import com.zephyr.ai.domain.auth.model.entity.LoginCommandEntity;
import com.zephyr.ai.domain.auth.model.vo.LoginUserVO;

/**
 * 认证领域服务。
 */
public interface IAuthService {

    /**
     * 执行登录。
     */
    LoginUserVO login(LoginCommandEntity command);
}
