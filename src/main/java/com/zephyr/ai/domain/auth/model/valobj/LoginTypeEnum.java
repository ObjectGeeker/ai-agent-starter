package com.zephyr.ai.domain.auth.model.valobj;

/**
 * 登录方式。
 * <p>
 * 当前仅支持用户名密码登录，后续可在不改变登录服务调用方式的前提下扩展其它策略。
 */
public enum LoginTypeEnum {

    /**
     * 用户名密码登录。
     */
    USERNAME_PASSWORD;

    /**
     * 按接口传入值解析登录方式，忽略首尾空格和大小写。
     *
     * @param value 接口传入的登录方式
     * @return 匹配的登录方式；不支持或为空时返回 {@code null}
     */
    public static LoginTypeEnum from(String value) {
        if (value == null) {
            return null;
        }
        for (LoginTypeEnum loginType : values()) {
            if (loginType.name().equalsIgnoreCase(value.trim())) {
                return loginType;
            }
        }
        return null;
    }
}
