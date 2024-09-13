package com.wilbert.remita.vota.system.controller;

import com.wilbert.remita.vota.auth.repository.CandidateRepository;
import com.wilbert.remita.vota.system.dto.PollDto;
import com.wilbert.remita.vota.system.entity.Candidate;
import com.wilbert.remita.vota.system.entity.Poll;
import com.wilbert.remita.vota.system.service.CandidateService;
import com.wilbert.remita.vota.system.service.PollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/polls")
@RequiredArgsConstructor
public class PollController {

    private final PollService pollService;
    private final CandidateService candidateService;
    private final CandidateRepository candidateRepository;


    @GetMapping("/view")
    public ResponseEntity<List<Poll>> viewAllPolls(){
        List<Poll> polls = pollService.getAllPolls();

        if(polls != null){
            return ResponseEntity.ok(polls);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Poll> viewPoll(@PathVariable Integer id){
        Poll poll = pollService.getPollById(id);
        if(poll != null){
            return ResponseEntity.ok(poll);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/vote")
    public ResponseEntity<?> vote(@PathVariable int id, int candidateId){
        try{
            if(candidateId < 0 || candidateId >= pollService.getPollById(id).getCandidateNames().size()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Candidate ID invalid");
            } else {
                candidateService.vote(candidateId);
            }
            return ResponseEntity.status(HttpStatus.OK).body("Vote successful");
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while voting!");
        }
    }

    @PostMapping("/create")
    public ResponseEntity<String> createPoll(@RequestBody PollDto pollDto){
       try{
           Poll poll = new Poll();
           poll.setQuestion(pollDto.getQuestion());
           poll.setPollExpiresAt(pollDto.getExpirationTime());


           List<String> candidateNames = new ArrayList<>();
           candidateNames.addAll(pollDto.getCandidateNames());
           poll.setCandidateNames(candidateNames);
           pollService.createPoll(poll);

           return ResponseEntity.status(HttpStatus.CREATED).body("Poll created successfully");
       } catch(Exception e){
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while creating poll!");
       }
    }



}
