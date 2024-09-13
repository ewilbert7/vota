package com.wilbert.remita.vota.system.service;

import com.wilbert.remita.vota.system.entity.Poll;
import com.wilbert.remita.vota.system.repository.PollRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository pollRepository;

    public List<Poll> getAllPolls(){
        return pollRepository.findAll();
    }

    public Poll getPollById(int id){
        return pollRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Poll not found")
        );
    }

    @Transactional
    public void createPoll(Poll poll){
        pollRepository.save(poll);
    }


}
