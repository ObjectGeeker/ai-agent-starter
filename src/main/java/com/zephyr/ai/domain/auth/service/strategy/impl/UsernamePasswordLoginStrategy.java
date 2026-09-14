package com.zephyr.ai.domain.auth.service.strategy.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.object.common.brick.common.ErrorCode;
import com.object.common.brick.exception.BusinessException;
import com.zephyr.ai.domain.auth.model.entity.LoginCommandEntity;
import com.zephyr.ai.domain.auth.model.valobj.LoginTypeEnum;
import com.zephyr.ai.domain.auth.model.vo.LoginUserVO;
import com.zephyr.ai.domain.auth.service.strategy.LoginStrategy;
import com.zephyr.ai.domain.user.model.po.UserPO;
import com.zephyr.ai.infrastructure.user.mapper.UserMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 用户名密码登录策略。
 */
@Component
public class UsernamePasswordLoginStrategy implements LoginStrategy {

    private static final String AUTHENTICATION_FAILED_MESSAGE = "用户名或密码错误";

    private final UserMapper userMapper;

    public UsernamePasswordLoginStrategy(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public LoginTypeEnum getLoginType() {
        return LoginTypeEnum.USERNAME_PASSWORD;
    }

    @Override
    public LoginUserVO login(LoginCommandEntity command) {
        String username = command.getUsername();
        if (!StringUtils.hasText(username)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, AUTHENTICATION_FAILED_MESSAGE);
        }
        username = username.trim();
        UserPO user = userMapper.selectOne(new LambdaQueryWrapper<UserPO>()
                .eq(UserPO::getUsername, username));

        if (!isValidUser(user, command.getPassword())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, AUTHENTICATION_FAILED_MESSAGE);
        }

        StpUtil.login(user.getId());
        return LoginUserVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .build();
    }

    private boolean isValidUser(UserPO user, String password) {
        if (user == null || !Integer.valueOf(1).equals(user.getStatus())
                || !StringUtils.hasText(user.getPasswordHash()) || !StringUtils.hasText(password)) {
            return false;
        }
        try {
            return BCrypt.checkpw(password, user.getPasswordHash());
        } catch (IllegalArgumentException exception) {
            // 数据库中存在非法哈希时按认证失败处理，不向客户端暴露存储细节。
            return false;
        }
    }
}
