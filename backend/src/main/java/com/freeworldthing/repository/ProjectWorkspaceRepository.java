package com.freeworldthing.repository;

import com.freeworldthing.model.ProjectWorkspace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectWorkspaceRepository extends JpaRepository<ProjectWorkspace, Long> {
    Optional<ProjectWorkspace> findByContractId(Long contractId);
}
