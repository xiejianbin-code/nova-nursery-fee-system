package com.nova.backend.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 菜单实体类
 *
 * @author Nova
 */
@Data
@TableName("t_menu")
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long id;

    private String menuName;

    private Long parentId;

    private Integer menuType;

    private String routePath;

    private String componentPath;

    private String permission;

    private String icon;

    private Integer sort;

    private Integer status;

    private LocalDateTime createTime;

    private Long createUser;

    private LocalDateTime updateTime;

    private Long updateUser;

    @TableLogic
    private Integer deleted;
}