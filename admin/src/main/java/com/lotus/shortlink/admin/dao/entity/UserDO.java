package com.lotus.shortlink.admin.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.lotus.shortlink.admin.common.database.BaseDO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@TableName("t_user")
@Data
public class UserDO extends BaseDO implements Serializable{

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * username
     */
    private String username;

    /**
     * password
     */
    private String password;

    /**
     * real_name
     */
    private String realName;

    /**
     * phone
     */
    private String phone;

    /**
     * mail
     */
    private String mail;

    /**
     * deletion_time
     */
    private Date deletionTime;
}
