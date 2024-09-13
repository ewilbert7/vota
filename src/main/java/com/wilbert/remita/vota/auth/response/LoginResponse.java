package com.wilbert.remita.vota.auth.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

    private String token;

    private long expiresIn;

    public LoginResponse(long expiresIn, String token) {
        this.expiresIn = expiresIn;
        this.token = token;
    }
}
