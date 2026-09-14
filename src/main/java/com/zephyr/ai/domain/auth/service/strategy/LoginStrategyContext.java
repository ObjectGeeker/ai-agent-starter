package com.zephyr.ai.domain.auth.service.strategy;

import com.object.common.brick.common.ErrorCode;
import com.object.common.brick.exception.BusinessException;
import com.zephyr.ai.domain.auth.model.entity.LoginCommandEntity;
import com.zephyr.ai.domain.auth.model.valobj.LoginTypeEnum;
import com.zephyr.ai.domain.auth.model.vo.LoginUserVO;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 登录策略上下文，根据登录方式选择具体策略。
 */
@Component
public class LoginStrategyContext {

    private final Map<LoginTypeEnum, LoginStrategy> strategyMap;

    public LoginStrategyContext(List<LoginStrategy> strategies) {
        EnumMap<LoginTypeEnum, LoginStrategy> mappings = new EnumMap<>(LoginTypeEnum.class);
        for (LoginStrategy strategy : strategies) {
            LoginStrategy previous = mappings.put(strategy.getLoginType(), strategy);
            if (previous != null) {
                throw new IllegalStateException("登录策略重复：" + strategy.getLoginType());
            }
        }
        this.strategyMap = Map.copyOf(mappings);
    }

    /**
     * 根据登录方式执行登录。
     */
    public LoginUserVO login(LoginCommandEntity command) {
        if (command == null || command.getLoginType() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "登录方式不能为空");
        }
        LoginStrategy strategy = strategyMap.get(command.getLoginType());
        if (strategy == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的登录方式");
        }
        return strategy.login(command);
    }
}
