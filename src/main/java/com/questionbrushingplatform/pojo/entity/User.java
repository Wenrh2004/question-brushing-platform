package com.questionbrushingplatform.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    @TableField("userAccount")
    private String userAccount;

    @TableField("userPassword")
    private String userPassword;

    @TableField("userName")
    private String userName;

    @TableField("userAvatar")
    private String userAvatar;

    @TableField("userProfile")
    private String userProfile;

    @TableField("userRole")
    private String userRole;

    @TableField("editTime")
    private LocalDateTime editTime;

    @TableField("createTime")
    private LocalDateTime createTime;

    @TableField("updateTime")
    private LocalDateTime updateTime;

    @TableField("isDelete")
    private Integer isDelete;

}
