package com.freeworldthing.repository;

import com.freeworldthing.model.ProofOfWork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProofOfWorkRepository extends JpaRepository<ProofOfWork, Long> {
    List<ProofOfWork> findByUserId(Long userId);
}
