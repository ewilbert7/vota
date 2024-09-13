package com.wilbert.remita.vota.auth.controller;

import com.wilbert.remita.vota.auth.dto.RegisterUserDto;
import com.wilbert.remita.vota.auth.dto.VerifyUserDto;
import com.wilbert.remita.vota.auth.service.AuthenticationService;
import com.wilbert.remita.vota.auth.user.User;
import com.wilbert.remita.vota.auth.dto.LoginCandidateDto;
import com.wilbert.remita.vota.auth.response.LoginResponse;
import com.wilbert.remita.vota.auth.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/candidates/auth")
public class CandidateAuthController extends AuthController {

    public CandidateAuthController(AuthenticationService authenticationService, JwtService jwtService) {
        super(authenticationService, jwtService);
    }

    @CrossOrigin(origins = "http://localhost:5173")
    @PostMapping("/signup")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerCandidateDto) {
        System.out.println("Received request to register candidate: " + registerCandidateDto.getEmail());
        User registeredUser = authenticationService.signupCandidate(registerCandidateDto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginCandidateDto loginCandidateDto) {
        User authenticatedUser = authenticationService.authenticateCandidate(loginCandidateDto);
        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse(jwtService.getExpirationTime(), jwtToken);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestBody VerifyUserDto verifyUserDto) {
        try {
            authenticationService.verifyCandidate(verifyUserDto);
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
