package com.wilbert.remita.vota.auth.controller;

import com.wilbert.remita.vota.auth.dto.LoginUserDto;
import com.wilbert.remita.vota.auth.dto.RegisterUserDto;
import com.wilbert.remita.vota.auth.dto.VerifyUserDto;
import com.wilbert.remita.vota.auth.response.LoginResponse;
import com.wilbert.remita.vota.auth.service.AuthenticationService;
import com.wilbert.remita.vota.auth.service.JwtService;
import com.wilbert.remita.vota.auth.user.User;
import com.wilbert.remita.vota.auth.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin/auth") @Slf4j
public class AdminAuthController extends AuthController {

   public AdminAuthController(AuthenticationService authenticationService,
                              JwtService jwtService, UserService userService) {
       super(authenticationService,jwtService);
   }

    @PostMapping("/signup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> register(@RequestBody RegisterUserDto registerAdminDto) {
       log.info("registerAdminDto: {}", registerAdminDto);
        User registeredAdmin = authenticationService.signupAdmin(registerAdminDto);
        return ResponseEntity.ok(registeredAdmin);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        User admin = authenticationService.authenticateAdmin(loginUserDto);
        String jwtToken = jwtService.generateToken(admin);

        LoginResponse loginResponse = new LoginResponse(jwtService.getExpirationTime(), jwtToken);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyUserDto verifyUserDto) {
        try {
            authenticationService.verifyAdmin(verifyUserDto);
            return ResponseEntity.ok("Admin verified successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
