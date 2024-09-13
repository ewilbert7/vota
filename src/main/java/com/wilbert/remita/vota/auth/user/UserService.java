package com.wilbert.remita.vota.auth.user;

import com.wilbert.remita.vota.auth.repository.CandidateRepository;
import com.wilbert.remita.vota.auth.repository.VoterRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final VoterRepository voterRepository;
    private final CandidateRepository candidateRepository;

    private static final String USER_NOT_FOUND_MSG="User with email %s not found";
    public UserService(VoterRepository voterRepository, CandidateRepository candidateRepository) {
        this.voterRepository = voterRepository;
        this.candidateRepository = candidateRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return voterRepository.findByEmail(username)
                .or(()->candidateRepository.findByEmail(username))
                .orElseThrow(()->new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG,username)));
    }
}
