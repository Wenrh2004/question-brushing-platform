package com.questionbrushingplatform.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author 永
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO{

    private Long id;

    private String userName;

    private String userAvatar;

    private String userProfile;


}