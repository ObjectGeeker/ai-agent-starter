package com.zephyr.ai.domain.auth.service.strategy;

import com.zephyr.ai.domain.auth.model.entity.LoginCommandEntity;
import com.zephyr.ai.domain.auth.model.valobj.LoginTypeEnum;
import com.zephyr.ai.domain.auth.model.vo.LoginUserVO;

/**
 * 登录策略。
 */
public interface LoginStrategy {

    /**
     * 返回当前策略支持的登录方式。
     */
    LoginTypeEnum getLoginType();

    /**
     * 执行登录并建立登录会话。
     */
    LoginUserVO login(LoginCommandEntity command);
}
