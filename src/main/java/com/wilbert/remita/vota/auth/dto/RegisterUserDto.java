package com.wilbert.remita.vota.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserDto {

    private String name;

    private String username;

    private String email;

    private String password;

//    private UserRole role = UserRole.VOTER;

}
