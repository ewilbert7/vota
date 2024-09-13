package com.wilbert.remita.vota.system.repository;

import com.wilbert.remita.vota.system.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateRepository2 extends JpaRepository<Candidate, Integer> {


}
