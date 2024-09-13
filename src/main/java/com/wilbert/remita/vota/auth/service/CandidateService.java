package com.wilbert.remita.vota.auth.service;

import com.wilbert.remita.vota.auth.repository.CandidateRepository;
import com.wilbert.remita.vota.auth.user.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("candidateAuthService")
public class CandidateService {

    private final CandidateRepository candidateRepository;

    public CandidateService(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }
    
    public List<User> getCandidates() {
        List<User> candidates = new ArrayList<>();
        candidateRepository.findAll().forEach(candidates::add);
        return candidates;
    }

}
