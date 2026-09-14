package com.zephyr.ai.domain.auth.service.impl;

import com.zephyr.ai.domain.auth.model.entity.LoginCommandEntity;
import com.zephyr.ai.domain.auth.model.vo.LoginUserVO;
import com.zephyr.ai.domain.auth.service.IAuthService;
import com.zephyr.ai.domain.auth.service.strategy.LoginStrategyContext;
import org.springframework.stereotype.Service;

/**
 * 认证领域服务默认实现。
 */
@Service
public class AuthServiceImpl implements IAuthService {

    private final LoginStrategyContext loginStrategyContext;

    public AuthServiceImpl(LoginStrategyContext loginStrategyContext) {
        this.loginStrategyContext = loginStrategyContext;
    }

    @Override
    public LoginUserVO login(LoginCommandEntity command) {
        return loginStrategyContext.login(command);
    }
}
