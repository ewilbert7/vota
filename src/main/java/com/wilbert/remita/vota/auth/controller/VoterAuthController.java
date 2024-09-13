package com.wilbert.remita.vota.auth.controller;

import com.wilbert.remita.vota.auth.dto.LoginVoterDto;
import com.wilbert.remita.vota.auth.dto.RegisterUserDto;
import com.wilbert.remita.vota.auth.dto.VerifyUserDto;
import com.wilbert.remita.vota.auth.service.AuthenticationService;
import com.wilbert.remita.vota.auth.user.User;
import com.wilbert.remita.vota.auth.response.LoginResponse;
import com.wilbert.remita.vota.auth.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/voters/auth")
public class VoterAuthController extends AuthController {

    private static final Logger log = LoggerFactory.getLogger(VoterAuthController.class);

    public VoterAuthController(AuthenticationService authenticationService, JwtService jwtService) {
        super(authenticationService, jwtService);
    }

    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        log.info("registerUserDto: {}", registerUserDto);
        User registeredUser = authenticationService.signupVoter(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginVoterDto loginVoterDto) {
        User authenticatedUser = authenticationService.authenticateVoter(loginVoterDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse(jwtService.getExpirationTime(), jwtToken);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyVoterDto) {
        try {
            authenticationService.verifyVoter(verifyVoterDto);
            return ResponseEntity.ok("Account verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resend")
    public ResponseEntity<?> resendVerificationCode(@RequestParam String email) {
        try{
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code resent");
        } catch(RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
