package com.lotus.shortlink.admin.dto.req;

import lombok.Data;

@Data
public class UserUpdateReqDTO {
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
}
