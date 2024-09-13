package com.wilbert.remita.vota.system.service;

import com.wilbert.remita.vota.system.entity.Candidate;
import com.wilbert.remita.vota.system.repository.CandidateRepository2;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("candidateService")
@Transactional
@RequiredArgsConstructor
public class CandidateService {

    private CandidateRepository2 candidateRepository;

    public List<Candidate> getCandidates() {
        return candidateRepository.findAll();
    }

    public void vote(int candidateId){
        Candidate candidate = candidateRepository.findById(candidateId).orElseThrow
                (()-> new RuntimeException("No candidate found with id: " + candidateId));

        candidate.setVoteCount(candidate.getVoteCount() + 1);
        candidateRepository.save(candidate);
    }


}
