package com.wilbert.remita.vota.system.entity;

import com.wilbert.remita.vota.auth.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String question;

    private LocalDateTime pollExpiresAt;

    @ElementCollection
    private List<String> candidateNames;





}
