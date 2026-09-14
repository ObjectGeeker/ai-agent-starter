package com.zephyr.ai.api;

import com.object.common.brick.common.BaseResponse;
import com.object.common.brick.common.ErrorCode;
import com.object.common.brick.exception.BusinessException;
import com.object.common.brick.utils.ResultUtil;
import com.zephyr.ai.api.model.request.LoginRequest;
import com.zephyr.ai.api.model.response.LoginUserResponse;
import com.zephyr.ai.domain.auth.model.entity.LoginCommandEntity;
import com.zephyr.ai.domain.auth.model.valobj.LoginTypeEnum;
import com.zephyr.ai.domain.auth.model.vo.LoginUserVO;
import com.zephyr.ai.domain.auth.service.IAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口。
 */
@RestController
@RequestMapping("auth")
@Tag(name = "认证接口")
public class AuthController {

    private final IAuthService authService;

    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    @PostMapping("login")
    @Operation(summary = "用户登录")
    public BaseResponse<LoginUserResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginTypeEnum loginType = LoginTypeEnum.from(request.getLoginType());
        if (loginType == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的登录方式");
        }
        LoginUserVO user = authService.login(LoginCommandEntity.builder()
                .loginType(loginType)
                .username(request.getUsername())
                .password(request.getPassword())
                .build());
        return ResultUtil.success(LoginUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .build());
    }
}
