CREATE TABLE IF NOT EXISTS sys_user
(
    id            VARCHAR(64)           NOT NULL COMMENT '用户 ID',
    username      VARCHAR(64)                    DEFAULT NULL COMMENT '登录账号',
    password_hash VARCHAR(255)                   DEFAULT NULL COMMENT 'BCrypt 密码哈希',
    nickname      VARCHAR(64)           NOT NULL DEFAULT '' COMMENT '用户昵称',
    avatar        VARCHAR(512)                   DEFAULT NULL COMMENT '用户头像地址',
    roles         JSON                  NOT NULL COMMENT '角色编码 JSON 字符串数组，例如 ["USER"]',
    wx_openid     VARCHAR(64)                    DEFAULT NULL COMMENT '微信 OpenID',
    status        TINYINT UNSIGNED      NOT NULL DEFAULT 1 COMMENT '用户状态：1 正常，0 禁用',
    create_user   VARCHAR(64)                    DEFAULT NULL COMMENT '创建人',
    create_time   DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_user   VARCHAR(64)                    DEFAULT NULL COMMENT '更新人',
    update_time   DATETIME              NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    is_delete     TINYINT(1)            NOT NULL DEFAULT 0 COMMENT '逻辑删除：0 未删除，1 已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_user_username (username),
    UNIQUE KEY uk_sys_user_wx_openid (wx_openid)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '系统用户表';
