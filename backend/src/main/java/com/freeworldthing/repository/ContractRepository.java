package com.freeworldthing.repository;

import com.freeworldthing.model.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    List<Contract> findByClientId(Long clientId);
    List<Contract> findByFreelancerId(Long freelancerId);
    List<Contract> findByClientIdOrderByCreatedAtDesc(Long clientId);
    List<Contract> findByFreelancerIdOrderByCreatedAtDesc(Long freelancerId);
}
