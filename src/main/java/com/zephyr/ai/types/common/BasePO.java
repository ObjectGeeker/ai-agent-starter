package com.zephyr.ai.types.common;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BasePO {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @TableField(value = "create_user")
    private String createUser;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "update_user")
    private String updateUser;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    @TableLogic(value = "is_delete", delval = "1")
    private Boolean isDelete;

}
