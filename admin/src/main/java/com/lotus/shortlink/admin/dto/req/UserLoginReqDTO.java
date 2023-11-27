package com.lotus.shortlink.admin.dto.req;

import lombok.Data;

@Data
public class UserLoginReqDTO {
    /**
     * username
     */
    private String username;

    /**
     * password
     */
    private String password;
}
