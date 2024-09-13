package com.wilbert.remita.vota.system.dto;

import com.wilbert.remita.vota.auth.user.User;
import com.wilbert.remita.vota.system.entity.Candidate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PollDto {

    private String question;

    private LocalDateTime expirationTime;

    private List<String> candidateNames;

}
