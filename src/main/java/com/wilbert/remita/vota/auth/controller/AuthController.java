package com.wilbert.remita.vota.auth.controller;


import com.wilbert.remita.vota.auth.service.AuthenticationService;
import com.wilbert.remita.vota.auth.service.JwtService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    protected final AuthenticationService authenticationService;

    protected final JwtService jwtService;

    public AuthController(AuthenticationService authenticationService,
                          JwtService jwtService) {
        this.authenticationService = authenticationService;
        this.jwtService = jwtService;
    }




}
