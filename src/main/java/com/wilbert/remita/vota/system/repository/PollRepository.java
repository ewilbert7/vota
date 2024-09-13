package com.wilbert.remita.vota.system.repository;

import com.wilbert.remita.vota.system.entity.Poll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PollRepository extends JpaRepository<Poll, Integer> {


}
