package com.lotus.shortlink.admin.dto.resp;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lotus.shortlink.admin.common.serialize.PhoneDesensitizationSerializer;
import lombok.Data;

@Data
public class UserRespDTO {
    /**
     * id
     */
    private Long id;

    /**
     * username
     */
    private String username;

    /**
     * real_name
     */
    private String realName;

    /**
     * phone
     */
    @JsonSerialize(using = PhoneDesensitizationSerializer.class)
    private String phone;

    /**
     * mail
     */
    private String mail;
}
