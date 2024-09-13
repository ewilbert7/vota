package com.wilbert.remita.vota.auth.service;

import com.wilbert.remita.vota.auth.repository.CandidateRepository;
import com.wilbert.remita.vota.auth.user.User;
import com.wilbert.remita.vota.auth.repository.VoterRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VoterService {

    private final VoterRepository voterRepository;

    public VoterService(VoterRepository voterRepository,
                        CandidateRepository candidateRepository) {
        this.voterRepository = voterRepository;

    }

    public List<User> getVoters() {
        List<User> voters = new ArrayList<>();
        voterRepository.findAll().forEach(voters::add);
        return voters;
    }
}
