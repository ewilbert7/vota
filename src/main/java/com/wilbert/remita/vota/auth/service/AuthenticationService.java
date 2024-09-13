package com.wilbert.remita.vota.auth.service;


import com.wilbert.remita.vota.auth.dto.*;
import com.wilbert.remita.vota.auth.repository.AdminRepository;
import com.wilbert.remita.vota.auth.user.User;
import com.wilbert.remita.vota.auth.user.UserRole;
import com.wilbert.remita.vota.auth.repository.CandidateRepository;
import com.wilbert.remita.vota.auth.repository.VoterRepository;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;

@Service @Slf4j
public class AuthenticationService implements CommandLineRunner {

    private final VoterRepository voterRepository;
    private final CandidateRepository candidateRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthenticationService(VoterRepository userRepository,
                                 CandidateRepository candidateRepository,
                                 AdminRepository adminRepository,
                                 PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager,
                                 EmailService emailService) {

        this.voterRepository = userRepository;
        this.candidateRepository = candidateRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
    }

    private User signupUser(String name, String username, String email,
                            String password, UserRole role,
                            CrudRepository<User, Long> repository){
        User user = new User(name, username,
               email,
                passwordEncoder.encode(password),
                role);

        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(5));
        user.setEnabled(false);
//        sendVerificationEmail(user);
        return repository.save(user);

    }

    public User signupAdmin(RegisterUserDto input) {
        log.info("Registering new admin: {}", input.getUsername());
        return signupUser(input.getName(),input.getUsername(),
                input.getEmail(),
                input.getPassword(),
                UserRole.ADMIN,
                adminRepository);
    }

    public User signupVoter(RegisterUserDto input) {
        return signupUser(input.getName(),input.getUsername(),
                input.getEmail(),
                input.getPassword(),
                UserRole.VOTER,
                voterRepository);
    }

    public User signupCandidate(RegisterUserDto input) {
        return signupUser(input.getName(),input.getUsername(),
                input.getEmail(),
                input.getPassword(),
                UserRole.CANDIDATE,
                candidateRepository);
    }

    public String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    private void sendVerificationEmail(User user) {
        String subject = "Account verification";
        String verificationCode = user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationMail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email", e);
        }

    }

    public User authenticateVoter(LoginVoterDto input) {
        User user  = voterRepository.findByEmail((input.getEmail()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!user.isEnabled()){
            throw new RuntimeException("Account not verified. Please verify your account");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword())
        );

        return user;
    }

    public User authenticateCandidate(LoginCandidateDto input) {
        User user = candidateRepository.findByEmail((input.getEmail()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()){
            throw new RuntimeException("Account not verified. Please verify your account");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword())
        );

        return user;
    }

    public void verify(VerifyUserDto input, Optional<User> optionalUser,
                       CrudRepository<User, Long> repository) {
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())){
                throw new RuntimeException("Verification code expired");
            } if(user.getVerificationCode().equals(input.getVerificationCode())){
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpiresAt(null);
                repository.save(user);
            } else {
                throw new RuntimeException("Invalid verification code");
            }

        } else {
            throw new RuntimeException("User not found");
        }

    }

    public void verifyAdmin(VerifyUserDto input) {
        Optional<User> optionalUser = adminRepository.findByEmail(input.getEmail());
        verify(input, optionalUser, adminRepository);
    }

    public void verifyVoter(VerifyUserDto input) {
        Optional<User> optionalUser = voterRepository.findByEmail(input.getEmail());
        verify(input, optionalUser, voterRepository);
    }

    public void verifyCandidate(VerifyUserDto input) {
        Optional<User> optionalUser = candidateRepository.findByEmail(input.getEmail());
        verify(input, optionalUser, candidateRepository);
    }

    public void resendVerificationCode(String email) {
        Optional<User> optionalUser = voterRepository.findByEmail(email)
                .or(() -> candidateRepository.findByEmail(email));
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getUserRole() == UserRole.VOTER) {
                verificationHelper(voterRepository, user);
            } else if (user.getUserRole() == UserRole.CANDIDATE) {
                verificationHelper(candidateRepository, user);
            } else {
                throw new RuntimeException("Not a valid user");
            }

        } else {
            throw new RuntimeException("User not found");
        }

    }

    private void verificationHelper(CrudRepository<User, Long> repository, User user) {
        if (user.isEnabled()) {
            throw new RuntimeException("User already verified");
        }
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(5));
       // sendVerificationEmail(user);
        candidateRepository.save(user);
    }

    @Override
    public void run(String... args) throws Exception {
        if (adminRepository.count() == 0) {
            User admin = new User("Admin Man","admin","admin@user.com",
                    passwordEncoder.encode("12345"), UserRole.ADMIN);
            admin.setEnabled(true);
            adminRepository.save(admin);
        }
    }

    public User authenticateAdmin(LoginUserDto input) {
        User user = adminRepository.findByEmail((input.getEmail()))
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (!user.isEnabled()){
            throw new RuntimeException("Admin not verified. Please verify your account");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword())
        );

        return user;
    }
}
