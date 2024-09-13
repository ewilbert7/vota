package com.wilbert.remita.vota.system.entity;

import com.wilbert.remita.vota.auth.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Candidate extends User {

    private Integer voteCount;

    @ManyToOne
    @JoinColumn(name = "poll_id")
    private Poll poll;


}
